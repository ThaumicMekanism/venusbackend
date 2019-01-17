package venusbackend.simulator

/* ktlint-disable no-wildcard-imports */

import venus.Renderer
import venus.vfs.VirtualFileSystem
import venusbackend.*
import venusbackend.linker.LinkedProgram
import venusbackend.riscv.*
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.diffs.*

/* ktlint-enable no-wildcard-imports */

/** Right now, this is a loose wrapper around SimulatorState
    Eventually, it will support debugging. */
class Simulator(
    val linkedProgram: LinkedProgram,
    val VFS: VirtualFileSystem = VirtualFileSystem("dummy"),
    var settings: SimulatorSettings = SimulatorSettings(),
    val state: SimulatorState = SimulatorState32(),
    val simulatorID: Int = 0
) {

    private var cycles = 0
    private val history = History()
    private val preInstruction = ArrayList<Diff>()
    private val postInstruction = ArrayList<Diff>()
    private val breakpoints: Array<Boolean>
    var args = ArrayList<String>()
    var ebreak = false
    var stdout = ""
    var filesHandler = FilesHandler(this)
    val instOrderMapping = HashMap<Int, Int>()
    val invInstOrderMapping = HashMap<Int, Int>()

    init {
        (state).getReg(1)
        var i = 0
        for (inst in linkedProgram.prog.insts) {
            instOrderMapping[i] = state.getMaxPC().toInt()
            invInstOrderMapping[state.getMaxPC().toInt()] = i
            i++
            var mcode = inst[InstructionField.ENTIRE]
            for (j in 0 until inst.length) {
                state.mem.storeByte(state.getMaxPC(), mcode and 0xFF)
                mcode = mcode shr 8
                state.incMaxPC(1)
            }
        }

        var dataOffset = MemorySegments.STATIC_BEGIN
        for (datum in linkedProgram.prog.dataSegment) {
            state.mem.storeByte(dataOffset, datum.toInt())
            dataOffset++
        }

        setPC(linkedProgram.startPC ?: MemorySegments.TEXT_BEGIN)
        if (settings.setRegesOnInit) {
            state.setReg(2, MemorySegments.STACK_BEGIN)
            state.setReg(3, MemorySegments.STATIC_BEGIN)
        }

        breakpoints = Array(linkedProgram.prog.insts.size, { false })
    }

    fun isDone(): Boolean {
        return getPC() >= if (settings.ecallOnlyExit) {
            MemorySegments.STATIC_BEGIN
        } else {
            state.getMaxPC()
        }
    }

    fun getCycles(): Int {
        return cycles
    }

    fun getMaxPC(): Number {
        return state.getMaxPC()
    }

    fun incMaxPC(amount: Number) {
        state.incMaxPC(amount)
    }

    fun getInstAt(addr: Number): MachineCode {
        val instnum = invInstOrderMapping[addr]!!.toInt()
        return linkedProgram.prog.insts[instnum]
    }

    fun setMemory(mem: Memory) {
        state.mem = mem
    }

    fun run() {
        while (!isDone() && cycles <= settings.maxSteps) {
            step()
        }
        if (cycles > settings.maxSteps) {
            throw SimulatorError("Ran for more than max allowed steps!")
        }
    }

    fun step(): List<Diff> {
        this.branched = false
        this.jumped = false
        this.ebreak = false
        this.ecallMsg = ""
        cycles++
        preInstruction.clear()
        postInstruction.clear()
        val mcode: MachineCode = getNextInstruction()
        when (state.registerWidth) {
            16 -> { Instruction[mcode].impl16(mcode, this) }
            32 -> { Instruction[mcode].impl32(mcode, this) }
            64 -> { Instruction[mcode].impl64(mcode, this) }
            128 -> { Instruction[mcode].impl128(mcode, this) }
            else -> { throw SimulatorError("Unsupported register width!") }
        }
        history.add(preInstruction)
        this.stdout += this.ecallMsg
        return postInstruction.toList()
    }

    fun undo(): List<Diff> {
        if (!canUndo()) return emptyList() /* TODO: error here? */
        val diffs = history.pop()
        for (diff in diffs) {
            diff(state)
        }
        cycles--
        return diffs
    }

    fun removeAllArgsFromMem() {
        var sp = getReg(2)
        while (sp < MemorySegments.STACK_BEGIN && settings.setRegesOnInit) {
            this.state.mem.removeByte(sp)
            sp++
            setReg(2, sp)
        }
    }

    fun removeAllArgs() {
        removeAllArgsFromMem()
        this.args.removeAll(this.args)
    }

    fun removeArg(index: Int) {
        if (index in 0 until this.args.size) {
            this.args.removeAt(index)
            this.removeAllArgsFromMem()
            addArgsToMem()
        }
    }

    fun addArg(arg: String) {
        if (arg == "" || !settings.setRegesOnInit) {
            return
        }
        args.add(arg)
        var spv = if (getReg(2) == MemorySegments.STACK_BEGIN) {
            getReg(2)
        } else {
            getReg(11)
        } - 1
        storeByte(spv, 0)
        setReg(2, spv)
        for (c in arg.reversed()) {
            spv = getReg(2) - 1
            storeByte(spv, c.toInt())
            setReg(2, spv)
        }
        /*Got to add the null terminator as well!*/
        /**
         * We need to store a0 (x10) to the argc and a1 (x11) to argv.
         */
        setReg(10, args.size)
        setReg(11, spv)
        setReg(2, (spv - (spv % 4)))
        try {
            Renderer.updateRegister(2, getReg(2))
            Renderer.updateRegister(10, getReg(10))
            Renderer.updateRegister(11, getReg(11))
            Renderer.updateMemory(Renderer.activeMemoryAddress)
        } catch (e: Throwable) {}
    }

    fun addArgsToMem() {
        if (!settings.setRegesOnInit) {
            return
        }
        var spv = if (getReg(2) == MemorySegments.STACK_BEGIN) {
            getReg(2)
        } else {
            getReg(11)
        } - 1
        for (arg in args) {
            spv = getReg(2) - 1
            storeByte(spv, 0)
            setRegNoUndo(2, spv)
            for (c in arg.reversed()) {
                spv = getReg(2) - 1
                storeByte(spv, c.toInt())
                setRegNoUndo(2, spv)
            }
            /*Got to add the null terminator as well!*/
            /**
             * We need to store a0 (x10) to the argc and a1 (x11) to argv.
             */
            setRegNoUndo(10, args.size)
            setRegNoUndo(11, spv)
        }
        setRegNoUndo(2, spv - (spv % 4))
        try {
            Renderer.updateRegister(2, getReg(2))
            Renderer.updateRegister(10, getReg(10))
            Renderer.updateRegister(11, getReg(11))
            Renderer.updateMemory(Renderer.activeMemoryAddress)
        } catch (e: Throwable) {}
    }

    var ecallMsg = ""
    var branched = false
    var jumped = false
    fun reset() {
        while (this.canUndo()) {
            this.undo()
        }
        this.branched = false
        this.jumped = false
        this.ecallMsg = ""
        this.stdout = ""
        cycles = 0
        removeAllArgs()
    }

    fun trace(): Tracer {
        return Tracer(this)
    }

    fun canUndo() = !history.isEmpty()

    fun getReg(id: Int) = state.getReg(id)

    fun setReg(id: Int, v: Number) {
        preInstruction.add(RegisterDiff(id, getReg(id)))
        state.setReg(id, v)
        postInstruction.add(RegisterDiff(id, getReg(id)))
    }

    fun setRegNoUndo(id: Int, v: Number) {
        state.setReg(id, v)
    }

    fun getFReg(id: Int) = state.getFReg(id)

    fun setFReg(id: Int, v: Decimal) {
        preInstruction.add(FRegisterDiff(id, state.getFReg(id)))
        state.setFReg(id, v)
        postInstruction.add(FRegisterDiff(id, state.getFReg(id)))
    }

    fun setFRegNoUndo(id: Int, v: Decimal) {
        state.setFReg(id, v)
    }

    fun toggleBreakpointAt(idx: Int): Boolean {
        breakpoints[idx] = !breakpoints[idx]
        return breakpoints[idx]
    }

    /* TODO Make this more efficient while robust! */
    fun atBreakpoint(): Boolean {
        val location = (getPC() - MemorySegments.TEXT_BEGIN).toLong()
        val inst = invInstOrderMapping[location.toInt()]!!
        return ebreak || breakpoints[inst]
    }

    fun getPC() = state.getPC()

    fun setPC(newPC: Number) {
        preInstruction.add(PCDiff(getPC()))
        state.setPC(newPC)
        postInstruction.add(PCDiff(getPC()))
    }

    fun incrementPC(inc: Number) {
        preInstruction.add(PCDiff(getPC()))
        state.incPC(inc)
        postInstruction.add(PCDiff(getPC()))
    }

    fun loadByte(addr: Number): Int = state.mem.loadByte(addr)
    fun loadBytewCache(addr: Number): Int {
        if (this.settings.alignedAddress && addr % MemSize.BYTE.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not BYTE aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.BYTE)))
        state.cache.read(Address(addr, MemSize.BYTE))
        postInstruction.add(CacheDiff(Address(addr, MemSize.BYTE)))
        return this.loadByte(addr)
    }

    fun loadHalfWord(addr: Number): Int = state.mem.loadHalfWord(addr)
    fun loadHalfWordwCache(addr: Number): Int {
        if (this.settings.alignedAddress && addr % MemSize.HALF.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not HALF WORD aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.HALF)))
        state.cache.read(Address(addr, MemSize.HALF))
        postInstruction.add(CacheDiff(Address(addr, MemSize.HALF)))
        return this.loadHalfWord(addr)
    }

    fun loadWord(addr: Number): Int = state.mem.loadWord(addr)
    fun loadWordwCache(addr: Number): Int {
        if (this.settings.alignedAddress && addr % MemSize.WORD.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not WORD aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.WORD)))
        state.cache.read(Address(addr, MemSize.WORD))
        postInstruction.add(CacheDiff(Address(addr, MemSize.WORD)))
        return this.loadWord(addr)
    }

    fun loadLong(addr: Number): Long = state.mem.loadLong(addr)
    fun loadLongwCache(addr: Number): Long {
        if (this.settings.alignedAddress && addr % MemSize.LONG.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not LONG aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.LONG)))
        state.cache.read(Address(addr, MemSize.LONG))
        postInstruction.add(CacheDiff(Address(addr, MemSize.LONG)))
        return this.loadLong(addr)
    }

    fun storeByte(addr: Number, value: Number) {
        preInstruction.add(MemoryDiff(addr, loadWord(addr)))
        state.mem.storeByte(addr, value)
        postInstruction.add(MemoryDiff(addr, loadWord(addr)))
        this.storeTextOverrideCheck(addr, value, MemSize.BYTE)
    }
    fun storeBytewCache(addr: Number, value: Number) {
        if (this.settings.alignedAddress && addr % MemSize.BYTE.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not BYTE aligned!")
        }
        // FIXME change the cast to maxpc to something more generic or make the iterator be generic.
        if (!this.settings.mutableText && addr in (MemorySegments.TEXT_BEGIN + 1 - MemSize.BYTE.size)..state.getMaxPC().toInt()) {
            throw StoreError("You are attempting to edit the text of the program though the program is set to immutable at address " + Renderer.toHex(addr) + "!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.BYTE)))
        state.cache.write(Address(addr, MemSize.BYTE))
        this.storeByte(addr, value)
        postInstruction.add(CacheDiff(Address(addr, MemSize.BYTE)))
    }

    fun storeHalfWord(addr: Number, value: Number) {
        preInstruction.add(MemoryDiff(addr, loadWord(addr)))
        state.mem.storeHalfWord(addr, value)
        postInstruction.add(MemoryDiff(addr, loadWord(addr)))
        this.storeTextOverrideCheck(addr, value, MemSize.HALF)
    }
    fun storeHalfWordwCache(addr: Number, value: Number) {
        if (this.settings.alignedAddress && addr % MemSize.HALF.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not HALF WORD aligned!")
        }
        if (!this.settings.mutableText && addr in (MemorySegments.TEXT_BEGIN + 1 - MemSize.HALF.size)..state.getMaxPC().toInt()) {
            throw StoreError("You are attempting to edit the text of the program though the program is set to immutable at address " + Renderer.toHex(addr) + "!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.HALF)))
        state.cache.write(Address(addr, MemSize.HALF))
        this.storeHalfWord(addr, value)
        postInstruction.add(CacheDiff(Address(addr, MemSize.HALF)))
    }

    fun storeWord(addr: Number, value: Number) {
        preInstruction.add(MemoryDiff(addr, loadWord(addr)))
        state.mem.storeWord(addr, value)
        postInstruction.add(MemoryDiff(addr, loadWord(addr)))
        this.storeTextOverrideCheck(addr, value, MemSize.WORD)
    }
    fun storeWordwCache(addr: Number, value: Number) {
        if (this.settings.alignedAddress && addr % MemSize.WORD.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not WORD aligned!")
        }
        if (!this.settings.mutableText && addr in (MemorySegments.TEXT_BEGIN + 1 - MemSize.WORD.size)..state.getMaxPC().toInt()) {
            throw StoreError("You are attempting to edit the text of the program though the program is set to immutable at address " + Renderer.toHex(addr) + "!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.WORD)))
        state.cache.write(Address(addr, MemSize.WORD))
        this.storeWord(addr, value)
        postInstruction.add(CacheDiff(Address(addr, MemSize.WORD)))
    }

    fun storeLong(addr: Number, value: Number) {
        preInstruction.add(MemoryDiff(addr, loadLong(addr)))
        state.mem.storeLong(addr, value)
        postInstruction.add(MemoryDiff(addr, loadLong(addr)))
        this.storeTextOverrideCheck(addr, value, MemSize.LONG)
    }
    fun storeLongwCache(addr: Number, value: Number) {
        if (this.settings.alignedAddress && addr % MemSize.LONG.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not long aligned!")
        }
        if (!this.settings.mutableText && addr in (MemorySegments.TEXT_BEGIN + 1 - MemSize.WORD.size)..state.getMaxPC().toInt()) {
            throw StoreError("You are attempting to edit the text of the program though the program is set to immutable at address " + Renderer.toHex(addr) + "!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.LONG)))
        state.cache.write(Address(addr, MemSize.LONG))
        this.storeLong(addr, value)
        postInstruction.add(CacheDiff(Address(addr, MemSize.LONG)))
    }

    fun storeTextOverrideCheck(addr: Number, value: Number, size: MemSize) {
        /*Here, we will check if we are writing to memory*/
        if (addr in (MemorySegments.TEXT_BEGIN until state.getMaxPC().toInt()) || (addr + size.size - MemSize.BYTE.size) in (MemorySegments.TEXT_BEGIN until state.getMaxPC().toInt())) {
            try {
                val adjAddr = ((addr / MemSize.WORD.size) * MemSize.WORD.size)
                val lowerAddr = adjAddr - MemorySegments.TEXT_BEGIN
                var newInst = this.state.mem.loadWord(adjAddr)
                preInstruction.add(Renderer.updateProgramListing(lowerAddr / MemSize.WORD.size, newInst))
                if ((lowerAddr + MemorySegments.TEXT_BEGIN) != addr && (lowerAddr + MemSize.WORD.size - MemSize.BYTE.size) < state.getMaxPC()) {
                    newInst = this.state.mem.loadWord(adjAddr + MemSize.WORD.size)
                    preInstruction.add(Renderer.updateProgramListing((lowerAddr / MemSize.WORD.size) + 1, newInst))
                }
            } catch (e: Throwable) { /*This is to not error the tests.*/ }
        }
    }

    fun getHeapEnd() = state.getHeapEnd()

    fun addHeapSpace(bytes: Number) {
        preInstruction.add(HeapSpaceDiff(state.getHeapEnd()))
        state.incHeapEnd(bytes)
        postInstruction.add(HeapSpaceDiff(state.getHeapEnd()))
    }

    private fun getInstructionLength(short0: Int): Int {
        if ((short0 and 0b11) != 0b11) {
            return 2
        } else if ((short0 and 0b11111) != 0b11111) {
            return 4
        } else if ((short0 and 0b111111) == 0b011111) {
            return 6
        } else if ((short0 and 0b1111111) == 0b111111) {
            return 8
        } else {
            throw SimulatorError("instruction lengths > 8 not supported")
        }
    }

    fun getNextInstruction(): MachineCode {
        var instruction: ULong = loadHalfWord(getPC()).toULong()
        val length = getInstructionLength(instruction.toInt())
        for (i in 1 until length / 2) {
            val short = loadHalfWord(getPC() + 2).toULong()
            instruction = (short shl 16 * i) or instruction
        }
        val mcode = MachineCode(instruction.toInt())
        mcode.length = length
        return mcode
    }
}
