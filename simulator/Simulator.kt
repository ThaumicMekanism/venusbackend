package venusbackend.simulator

/* ktlint-disable no-wildcard-imports */

import venus.Renderer
import venus.vfs.VirtualFileSystem
import venusbackend.linker.LinkedProgram
import venusbackend.riscv.*
import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.diffs.*
import java.math.BigInteger

/* ktlint-enable no-wildcard-imports */

/** Right now, this is a loose wrapper around SimulatorState
    Eventually, it will support debugging. */
class Simulator(val linkedProgram: LinkedProgram, val VFS: VirtualFileSystem = VirtualFileSystem("dummy"), var settings: SimulatorSettings = SimulatorSettings(), val simulatorID: Int = 0) {
    val state = SimulatorState()
    // TODO make pc not rely on being an INT
    var maxpc = MemorySegments.TEXT_BEGIN
    private var cycles = 0
    private val history = History()
    private val preInstruction = ArrayList<Diff>()
    private val postInstruction = ArrayList<Diff>()
    private val breakpoints: Array<Boolean>
    var args = ArrayList<String>()
    var ebreak = false
    var stdout = ""
    var filesHandler = FilesHandler(this)
    val instOrderMapping = HashMap<BigInteger, BigInteger>()

    init {
        var i = 0.toBigInteger()
        for (inst in linkedProgram.prog.insts) {
            instOrderMapping[i] = maxpc.toBigInteger()
            i++
            var mcode = inst[InstructionField.ENTIRE]
            for (j in 0 until inst.length) {
                state.mem.storeByte(maxpc, mcode and 0xFF)
                mcode = mcode shr 8
                maxpc++
            }
        }

        var dataOffset = MemorySegments.STATIC_BEGIN
        for (datum in linkedProgram.prog.dataSegment) {
            state.mem.storeByte(dataOffset, datum.toInt())
            dataOffset++
        }

        state.pc = linkedProgram.startPC ?: MemorySegments.TEXT_BEGIN
        if (settings.setRegesOnInit) {
            state.setReg(2, MemorySegments.STACK_BEGIN.toBigInteger())
            state.setReg(3, MemorySegments.STATIC_BEGIN.toBigInteger())
        }

        breakpoints = Array<Boolean>(linkedProgram.prog.insts.size, { false })
    }

    fun isDone(): Boolean = getPC() >= if (settings.ecallOnlyExit) MemorySegments.STATIC_BEGIN else maxpc

    fun getCycles(): Int {
        return cycles
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
        when (settings.registerWidth) {
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
        var sp = getRegActive(2)
        while (sp < MemorySegments.STACK_BEGIN.toBigInteger() && settings.setRegesOnInit) {
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
        var spv = if (getRegActive(2) == MemorySegments.STACK_BEGIN.toBigInteger()) {
            getRegActive(2)
        } else {
            getRegActive(11)
        } - 1.toBigInteger()
        storeByteActive(spv, 0.toBigInteger())
        setRegActive(2, spv)
        for (c in arg.reversed()) {
            spv = getRegActive(2) - 1.toBigInteger()
            storeByteActive(spv, c.toInt().toBigInteger())
            setRegActive(2, spv)
        }
        /*Got to add the null terminator as well!*/
        /**
         * We need to store a0 (x10) to the argc and a1 (x11) to argv.
         */
        setRegActive(10, args.size.toBigInteger())
        setRegActive(11, spv)
        setRegActive(2, (spv - (spv % 4.toBigInteger())))
        try {
            Renderer.updateRegister(2, getRegActive(2))
            Renderer.updateRegister(10, getRegActive(10))
            Renderer.updateRegister(11, getRegActive(11))
            Renderer.updateMemory(Renderer.activeMemoryAddress)
        } catch (e: Throwable) {}
    }

    fun addArgsToMem() {
        if (!settings.setRegesOnInit) {
            return
        }
        var spv = if (getRegActive(2) == MemorySegments.STACK_BEGIN.toBigInteger()) {
            getRegActive(2)
        } else {
            getRegActive(11)
        } - 1.toBigInteger()
        for (arg in args) {
            spv = getRegActive(2) - 1.toBigInteger()
            storeByteActive(spv, 0.toBigInteger())
            setRegActiveNoUndo(2, spv)
            for (c in arg.reversed()) {
                spv = getRegActive(2) - 1.toBigInteger()
                storeByteActive(spv, c.toInt().toBigInteger())
                setRegActiveNoUndo(2, spv)
            }
            /*Got to add the null terminator as well!*/
            /**
             * We need to store a0 (x10) to the argc and a1 (x11) to argv.
             */
            setRegActiveNoUndo(10, args.size.toBigInteger())
            setRegActiveNoUndo(11, spv)
        }
        setRegActiveNoUndo(2, spv - (spv % 4.toBigInteger()))
        try {
            Renderer.updateRegister(2, getRegActive(2))
            Renderer.updateRegister(10, getRegActive(10))
            Renderer.updateRegister(11, getRegActive(11))
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

    // NOTE: I have opted to set the defualt getReg to 32 for simplicity
    fun getReg16(id: Int) = state.getReg16(id)
    fun getReg(id: Int) = state.getReg32(id)
    fun getReg64(id: Int) = state.getReg64(id)
    fun getRegAny(id: Int) = state.getRegAny(id)
    // I have set the return type to bigint because it will preserve the number regardelss of the size.
    fun getRegActive(id: Int): BigInteger {
        return when (settings.registerWidth) {
            16 -> getReg16(id).toInt().toBigInteger()
            32 -> getReg(id).toBigInteger()
            64 -> getReg64(id).toBigInteger()
            else -> getRegAny(id)
        }
    }

    fun setReg(id: Int, v: Short) {
        preInstruction.add(RegisterDiff16(id, getReg16(id)))
        state.setReg(id, v)
        postInstruction.add(RegisterDiff16(id, getReg16(id)))
    }

    fun setReg(id: Int, v: Int) {
        preInstruction.add(RegisterDiff(id, getReg(id)))
        state.setReg(id, v)
        postInstruction.add(RegisterDiff(id, getReg(id)))
    }

    fun setReg(id: Int, v: Long) {
        preInstruction.add(RegisterDiff64(id, getReg64(id)))
        state.setReg(id, v)
        postInstruction.add(RegisterDiff64(id, getReg64(id)))
    }

    fun setReg(id: Int, v: BigInteger) {
        preInstruction.add(RegisterDiffAny(id, getRegAny(id)))
        state.setReg(id, v)
        postInstruction.add(RegisterDiffAny(id, getRegAny(id)))
    }

    fun setRegActive(id: Int, v: BigInteger) {
        return when (settings.registerWidth) {
            16 -> setReg(id, v.toShort())
            32 -> setReg(id, v.toInt())
            64 -> setReg(id, v.toLong())
            else -> setReg(id, v)
        }
    }

    fun setRegNoUndo(id: Int, v: Short) {
        state.setReg(id, v)
    }

    fun setRegNoUndo(id: Int, v: Int) {
        state.setReg(id, v)
    }
    fun setRegNoUndo(id: Int, v: Long) {
        state.setReg(id, v)
    }
    fun setRegNoUndo(id: Int, v: BigInteger) {
        state.setReg(id, v)
    }
    fun setRegActiveNoUndo(id: Int, v: BigInteger) {
        return when (settings.registerWidth) {
            16 -> setRegNoUndo(id, v.toShort())
            32 -> setRegNoUndo(id, v.toInt())
            64 -> setRegNoUndo(id, v.toLong())
            else -> setRegNoUndo(id, v)
        }
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
    fun atBreakpoint() = breakpoints[instOrderMapping[(state.pc - MemorySegments.TEXT_BEGIN).toBigInteger()]!!.toInt()] || ebreak

    fun getPC() = state.pc

    fun setPC(newPC: Int) {
        preInstruction.add(PCDiff(state.pc))
        state.pc = newPC
        postInstruction.add(PCDiff(state.pc))
    }

    fun incrementPC(inc: Int) {
        preInstruction.add(PCDiff(state.pc))
        state.pc += inc
        postInstruction.add(PCDiff(state.pc))
    }

    fun loadByte(addr: Short): Short = state.mem.loadByte(addr)
    fun loadBytewCache(addr: Short): Short {
        if (this.settings.alignedAddress && addr % MemSize.BYTE.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr.toInt()) + "' is not BYTE aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr.toInt(), MemSize.BYTE)))
        state.cache.read(Address(addr.toInt(), MemSize.BYTE))
        postInstruction.add(CacheDiff(Address(addr.toInt(), MemSize.BYTE)))
        return this.loadByte(addr)
    }
    fun loadByte(addr: Int): Int = state.mem.loadByte(addr)
    fun loadBytewCache(addr: Int): Int {
        if (this.settings.alignedAddress && addr % MemSize.BYTE.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not BYTE aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.BYTE)))
        state.cache.read(Address(addr, MemSize.BYTE))
        postInstruction.add(CacheDiff(Address(addr, MemSize.BYTE)))
        return this.loadByte(addr)
    }
    fun loadByte(addr: Long): Long = state.mem.loadByte(addr)
    fun loadBytewCache(addr: Long): Long {
        if (this.settings.alignedAddress && addr % MemSize.BYTE.size != 0.toLong()) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not BYTE aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr.toInt(), MemSize.BYTE)))
        state.cache.read(Address(addr.toInt(), MemSize.BYTE))
        postInstruction.add(CacheDiff(Address(addr.toInt(), MemSize.BYTE)))
        return this.loadByte(addr)
    }
    fun loadByte(addr: BigInteger): BigInteger = state.mem.loadByte(addr)
    fun loadBytewCache(addr: BigInteger): BigInteger {
        if (this.settings.alignedAddress && addr % MemSize.BYTE.size.toBigInteger() != 0.toBigInteger()) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not BYTE aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr.toInt(), MemSize.BYTE)))
        state.cache.read(Address(addr.toInt(), MemSize.BYTE))
        postInstruction.add(CacheDiff(Address(addr.toInt(), MemSize.BYTE)))
        return this.loadByte(addr)
    }

    fun loadHalfWord(addr: Int): Int = state.mem.loadHalfWord(addr)
    fun loadHalfWordwCache(addr: Int): Int {
        if (this.settings.alignedAddress && addr % MemSize.HALF.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not HALF WORD aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.HALF)))
        state.cache.read(Address(addr, MemSize.HALF))
        postInstruction.add(CacheDiff(Address(addr, MemSize.HALF)))
        return this.loadHalfWord(addr)
    }

    fun loadWord(addr: Int): Int = state.mem.loadWord(addr)
    fun loadWordwCache(addr: Int): Int {
        if (this.settings.alignedAddress && addr % MemSize.WORD.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not WORD aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.WORD)))
        state.cache.read(Address(addr, MemSize.WORD))
        postInstruction.add(CacheDiff(Address(addr, MemSize.WORD)))
        return this.loadWord(addr)
    }

    fun loadLong(addr: Int): Long = state.mem.loadLong(addr)
    fun loadLongwCache(addr: Int): Long {
        if (this.settings.alignedAddress && addr % MemSize.LONG.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not LONG aligned!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.LONG)))
        state.cache.read(Address(addr, MemSize.LONG))
        postInstruction.add(CacheDiff(Address(addr, MemSize.LONG)))
        return this.loadLong(addr)
    }

    fun storeByteActive(addr: BigInteger, value: BigInteger) {
        return when (settings.registerWidth) {
            16 -> storeByte(addr.toShort(), value.toShort())
            32 -> storeByte(addr.toInt(), value.toInt())
            64 -> storeByte(addr.toLong(), value.toLong())
            else -> storeByte(addr, value)
        }
    }

    fun storeByte(addr: Short, value: Short) {
        preInstruction.add(MemoryDiff(addr.toInt(), loadWord(addr.toInt())))
        state.mem.storeByte(addr, value)
        postInstruction.add(MemoryDiff(addr.toInt(), loadWord(addr.toInt())))
        this.storeTextOverrideCheck(addr.toInt(), value.toInt(), MemSize.BYTE)
    }
    fun storeByte(addr: Int, value: Int) {
        preInstruction.add(MemoryDiff(addr, loadWord(addr)))
        state.mem.storeByte(addr, value)
        postInstruction.add(MemoryDiff(addr, loadWord(addr)))
        this.storeTextOverrideCheck(addr, value, MemSize.BYTE)
    }
    fun storeByte(addr: Long, value: Long) {
        preInstruction.add(MemoryDiff(addr.toInt(), loadWord(addr.toInt())))
        state.mem.storeByte(addr, value)
        postInstruction.add(MemoryDiff(addr.toInt(), loadWord(addr.toInt())))
        this.storeTextOverrideCheck(addr.toInt(), value.toInt(), MemSize.BYTE)
    }
    fun storeByte(addr: BigInteger, value: BigInteger) {
        preInstruction.add(MemoryDiff(addr.toInt(), loadWord(addr.toInt())))
        state.mem.storeByte(addr, value)
        postInstruction.add(MemoryDiff(addr.toInt(), loadWord(addr.toInt())))
        this.storeTextOverrideCheck(addr.toInt(), value.toInt(), MemSize.BYTE)
    }
    fun storeBytewCache(addr: Int, value: Int) {
        if (this.settings.alignedAddress && addr % MemSize.BYTE.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not BYTE aligned!")
        }
        if (!this.settings.mutableText && addr in (MemorySegments.TEXT_BEGIN + 1 - MemSize.BYTE.size)..this.maxpc) {
            throw StoreError("You are attempting to edit the text of the program though the program is set to immutable at address " + Renderer.toHex(addr) + "!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.BYTE)))
        state.cache.write(Address(addr, MemSize.BYTE))
        this.storeByte(addr, value)
        postInstruction.add(CacheDiff(Address(addr, MemSize.BYTE)))
    }

    fun storeHalfWord(addr: Int, value: Int) {
        preInstruction.add(MemoryDiff(addr, loadWord(addr)))
        state.mem.storeHalfWord(addr, value)
        postInstruction.add(MemoryDiff(addr, loadWord(addr)))
        this.storeTextOverrideCheck(addr, value, MemSize.HALF)
    }
    fun storeHalfWordwCache(addr: Int, value: Int) {
        if (this.settings.alignedAddress && addr % MemSize.HALF.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not HALF WORD aligned!")
        }
        if (!this.settings.mutableText && addr in (MemorySegments.TEXT_BEGIN + 1 - MemSize.HALF.size)..this.maxpc) {
            throw StoreError("You are attempting to edit the text of the program though the program is set to immutable at address " + Renderer.toHex(addr) + "!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.HALF)))
        state.cache.write(Address(addr, MemSize.HALF))
        this.storeHalfWord(addr, value)
        postInstruction.add(CacheDiff(Address(addr, MemSize.HALF)))
    }

    fun storeWord(addr: Int, value: Int) {
        preInstruction.add(MemoryDiff(addr, loadWord(addr)))
        state.mem.storeWord(addr, value)
        postInstruction.add(MemoryDiff(addr, loadWord(addr)))
        this.storeTextOverrideCheck(addr, value, MemSize.WORD)
    }
    fun storeWordwCache(addr: Int, value: Int) {
        if (this.settings.alignedAddress && addr % MemSize.WORD.size != 0) {
            throw AlignmentError("Address: '" + Renderer.toHex(addr) + "' is not WORD aligned!")
        }
        if (!this.settings.mutableText && addr in (MemorySegments.TEXT_BEGIN + 1 - MemSize.WORD.size)..this.maxpc) {
            throw StoreError("You are attempting to edit the text of the program though the program is set to immutable at address " + Renderer.toHex(addr) + "!")
        }
        preInstruction.add(CacheDiff(Address(addr, MemSize.WORD)))
        state.cache.write(Address(addr, MemSize.WORD))
        this.storeWord(addr, value)
        postInstruction.add(CacheDiff(Address(addr, MemSize.WORD)))
    }

    fun storeTextOverrideCheck(addr: Int, value: Int, size: MemSize) {
        /*Here, we will check if we are writing to memory*/
        if (addr in (MemorySegments.TEXT_BEGIN until this.maxpc) || (addr + size.size - MemSize.BYTE.size) in (MemorySegments.TEXT_BEGIN until this.maxpc)) {
            try {
                val adjAddr = ((addr / MemSize.WORD.size) * MemSize.WORD.size)
                val lowerAddr = adjAddr - MemorySegments.TEXT_BEGIN
                var newInst = this.state.mem.loadWord(adjAddr)
                preInstruction.add(Renderer.updateProgramListing(lowerAddr / MemSize.WORD.size, newInst))
                if ((lowerAddr + MemorySegments.TEXT_BEGIN) != addr && (lowerAddr + MemSize.WORD.size - MemSize.BYTE.size) < this.maxpc) {
                    newInst = this.state.mem.loadWord(adjAddr + MemSize.WORD.size)
                    preInstruction.add(Renderer.updateProgramListing((lowerAddr / MemSize.WORD.size) + 1, newInst))
                }
            } catch (e: Throwable) { /*This is to not error the tests.*/ }
        }
    }

    fun getHeapEnd() = state.heapEnd

    fun addHeapSpace(bytes: Int) {
        preInstruction.add(HeapSpaceDiff(state.heapEnd))
        state.heapEnd += bytes
        postInstruction.add(HeapSpaceDiff(state.heapEnd))
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
        var instruction = loadHalfWord(getPC())
        val length = getInstructionLength(instruction)
        for (i in 1 until length / 2) {
            val short = loadHalfWord(getPC() + 2)
            instruction = (short shl 16 * i) or instruction
        }
        val mcode = MachineCode(instruction)
        mcode.length = length
        return mcode
    }
}
