package venusbackend.simulator

import venus.Renderer
import venusbackend.plus
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Registers
import venusbackend.riscv.getRegNameFromIndex
import venusbackend.riscv.insts.dsl.formats.base.BTypeFormat
import venusbackend.riscv.insts.dsl.formats.base.ITypeFormat
import venusbackend.riscv.insts.dsl.formats.base.RTypeFormat
import venusbackend.riscv.insts.dsl.formats.base.STypeFormat
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.integer.base.i.jalr
import venusbackend.riscv.insts.integer.base.s.sw
import venusbackend.riscv.insts.integer.base.uj.jal
import venusbackend.simulator.diffs.MemoryDiff
import venusbackend.simulator.diffs.PCDiff
import venusbackend.simulator.diffs.RegisterDiff
import venusbackend.toHex

data class StateChange(val pre: Diff, val post: Diff)

class CallingConventionCheck(val sim: Simulator, val returnOnlya0: Boolean = false) {
    var errorCnt = 0

    var callerRegs = getCallerSavedRegisters()
    var calleeRegs = getCalleeSavedRegisters()
    val aRegs = getaRegs()
    val tRegs = gettRegs()
    val sRegs = getsRegs()

    // Checking Caller (temp) registers are defined
    var currentActiveRegs: BooleanArray = BooleanArray(32)
    // Checking if Callee (save) registers are defined
    var currentSavedRegs: BooleanArray = BooleanArray(32)

    var ActiveRegs: MutableList<BooleanArray> = ArrayList()
    var SavedRegs: MutableList<BooleanArray> = ArrayList()
    var returnAddresses: MutableList<Number> = ArrayList()
    var SavedRegsValues: MutableList<MutableList<Number>> = ArrayList()
    var prevPC = sim.getPC()

    fun run(): Int {
        for (i in calleeRegs) {
            currentSavedRegs[i] = true
        }
        currentActiveRegs[Registers.sp] = true
        while (!sim.isDone()) {
            val inst = sim.getNextInstruction()
            prevPC = sim.getPC()
            sim.step()
            val pre = ArrayList<Diff>()
            for (d in sim.preInstruction) {
                if (d is RegisterDiff || d is MemoryDiff || d is PCDiff) {
                    pre.add(d)
                }
            }
            val post = ArrayList<Diff>()
            for (d in sim.postInstruction) {
                if (d is RegisterDiff || d is MemoryDiff || d is PCDiff) {
                    post.add(d)
                }
            }
            val pcStateChange: ArrayList<StateChange> = ArrayList()
            val regStateChange: ArrayList<StateChange> = ArrayList()
            val memStateChange: ArrayList<StateChange> = ArrayList()
            for ((pre, post) in pre.zip(post)) {
                if (pre is RegisterDiff) {
                    regStateChange.add(StateChange(pre, post))
                } else if (pre is MemoryDiff) {
                    memStateChange.add(StateChange(pre, post))
                } else if (pre is PCDiff) {
                    pcStateChange.add(StateChange(pre, post))
                }
            }
            handleSourceRegisters(inst)
            for (pcSC in pcStateChange) {
                val pre = pcSC.pre as PCDiff
                val post = pcSC.post as PCDiff
                if (isReturn(inst, post)) {
                    handleReturn()
                }
                if (isCall(pre, post, inst)) {
                    handleCall(pre.pc + inst.length)
                }
            }
            for (regSC in regStateChange) {
                val pre = regSC.pre as RegisterDiff
                val post = regSC.post as RegisterDiff
                handleDstRegister(post, inst)
            }
            for (memSC in memStateChange) {
                val pre = memSC.pre as MemoryDiff
                val post = memSC.post as MemoryDiff
                if (isSave(inst)) {
                    val reg = inst[InstructionField.RS2]
                    currentSavedRegs[reg] = true
                }
            }
        }
        Renderer.printConsole("Found $errorCnt warnings!")
        return errorCnt
    }

    fun printViolation(s: String) {
        errorCnt++
        Renderer.displayError("[CC Violation]: (PC=${toHex(prevPC)}) $s ${getDbg()}")
    }

    fun printWarning(s: String) {
        Renderer.displayWarning("[CC Warning]: (PC=${toHex(prevPC)}) $s ${getDbg()}")
    }

    fun getRetAddr(): Number? {
        if (returnAddresses.lastIndex == -1) {
            return null
        }
        return returnAddresses[returnAddresses.lastIndex]
    }

    fun addRetAddr(n: Number) {
        returnAddresses.add(n)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun popRetAddr(): Number? {
        return returnAddresses.removeLastOrNull()
    }

    fun getDbg(): String {
        val idx = sim.invInstOrderMapping[prevPC]!!
        val dbg = sim.linkedProgram.dbg[idx]
        return "${dbg.programName}:${dbg.dbg.lineNo} ${dbg.dbg.line.trim()}"
    }

    fun handleDstRegister(post: RegisterDiff, mcode: MachineCode) {
        // We have an error if we are not x0, we are a save (callee) register and we have not see some 'save' action.
        if (post.id != 0 && post.id in sRegs && !currentSavedRegs[post.id]) {
            printViolation("Setting of a saved register (${getRegNameFromIndex(post.id)}) which has not been saved!")
        }
//        if (isMV(post, mcode)) {
//            printWarning("Detected move/copy of a save register ${getRegNameFromIndex(post.id)} to another register! Will treat it as being saved. You should be using the stack to save these registers.")
//            currentSavedRegs[mcode[InstructionField.RD]] = true
//        }
        currentActiveRegs[mcode[InstructionField.RD]] = true
    }

    fun handleSourceRegisters(mcode: MachineCode) {
        val srcRegs = getSourceRegs(mcode)
        for (reg in srcRegs) {
            if (reg != 0 && (!currentActiveRegs[reg])) {
                printViolation("Usage of unset register ${getRegNameFromIndex(reg)}!")
            }
        }
    }

    fun getSourceRegs(mcode: MachineCode): List<Int> {
        val regs = ArrayList<Int>()
        val inst = Instruction[mcode]
        if (inst.format is RTypeFormat || inst.format is ITypeFormat || inst.format is STypeFormat || inst.format is BTypeFormat) {
            regs.add(mcode[InstructionField.RS1])
        }
        if (inst.format is RTypeFormat || inst.format is BTypeFormat) {
            regs.add(mcode[InstructionField.RS2])
        }
        return regs
    }

    fun isReturn(mcode: MachineCode, newPCDiff: PCDiff): Boolean {
        val inst = Instruction[mcode]
        return if (inst.name == jalr.name) {
            mcode[InstructionField.RD] == Registers.zero && mcode[InstructionField.RS1] == Registers.ra && signExtend(mcode[InstructionField.IMM_11_0].toInt(), 12) == 0 && newPCDiff.pc == getRetAddr()
        } else {
            false
        }
    }

    fun isCall(pre: PCDiff, post: PCDiff, mcode: MachineCode): Boolean {
        val inst = Instruction[mcode]
        return if (post.pc != pre.pc + mcode.length) {
            inst.name == jal.name && sim.linkedProgram.prog.isAddrGlobalLabel(post.pc)
        } else {
            false
        }
    }

    fun isSave(mcode: MachineCode): Boolean {
        val inst = Instruction[mcode]
        return inst.name == sw.name
    }

    // Currently dont wanna support move. Too difficult
    fun isMV(post: RegisterDiff, mcode: MachineCode): Boolean {
        val srcRegs = getSourceRegs(mcode)
        when (srcRegs.size) {
            0 -> {
                return false
            }
            1 -> {
                return post.v == sim.getReg(srcRegs[0])
            }
            2 -> {
                if (srcRegs[0] == 0 && srcRegs[1] in sRegs) {
                    return post.v == sim.getReg(srcRegs[1])
                } else if (srcRegs[1] == 0 && srcRegs[0] in sRegs) {
                    return post.v == sim.getReg(srcRegs[0])
                } else {
                    return false
                }
            }
        }
        return false
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun handleReturn() {
        val a = SavedRegsValues.removeLast()
        for (i in calleeRegs.withIndex()) {
            val exp = a[i.index]
            val act = sim.getReg(i.value)
            if (exp != act) {
                printViolation("Save register ${getRegNameFromIndex(i.value)} not correctly restored before return! Expected ${toHex(exp)}, Actual ${toHex(act)}.")
            }
        }
        if (this.returnOnlya0) {
            val a0Active = currentActiveRegs[Registers.a0]
//            currentActiveRegs = ActiveRegs.removeLastOrNull() ?: BooleanArray(currentActiveRegs.size)
            currentActiveRegs = ActiveRegs.removeLast()
            currentActiveRegs[Registers.a0] = a0Active
        } else {
            // Generic handler for keeping a registers.
            copyOverARegs(false)
        }
//        currentSavedRegs = SavedRegs.removeLastOrNull() ?: BooleanArray(currentSavedRegs.size)
        currentSavedRegs = SavedRegs.removeLast()
//        val esp = popStackPtr()
//        val asp = sim.getReg(Registers.sp)
//        if (esp != asp) {
//            errorCnt++
//            Renderer.displayError("[CC Warning]: Stack pointer not correctly restored at PC=${toHex(prevPC)}! ${getDbg()}")
//        }
        popRetAddr()
    }

    fun handleCall(nextPC: Number) {
        val a = ArrayList<Number>()
        SavedRegsValues.add(a)
        for (i in calleeRegs) {
            a.add(sim.getReg(i))
        }
        addRetAddr(nextPC)
//        addStackPtr(sim.getReg(Registers.sp))
        ActiveRegs.add(currentActiveRegs)
        SavedRegs.add(currentSavedRegs)
        copyOverARegs(true)
        currentSavedRegs = BooleanArray(currentSavedRegs.size)
        currentActiveRegs[Registers.sp] = true
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun copyOverARegs(new: Boolean) {
        val aregs = BooleanArray(aRegs.size)
        for (i in aRegs.withIndex()) {
            aregs[i.index] = currentActiveRegs[i.value]
        }
        currentActiveRegs = if (new) {
            BooleanArray(currentActiveRegs.size)
        } else {
//            ActiveRegs.removeLastOrNull() ?: BooleanArray(currentActiveRegs.size)
            ActiveRegs.removeLast()
        }
        for (i in aRegs.withIndex()) {
            currentActiveRegs[i.value] = aregs[i.index]
        }
    }

    fun getCallerSavedRegisters(): List<Int> {
        val callerSaveRegisters: ArrayList<Int> = arrayListOf(Registers.ra)
        callerSaveRegisters.addAll(gettRegs())
        callerSaveRegisters.addAll(getaRegs())
        return callerSaveRegisters
    }

    fun getCalleeSavedRegisters(): List<Int> {
        val calleeSaveRegisters: ArrayList<Int> = arrayListOf(Registers.sp)
//        val calleeSaveRegisters: ArrayList<Int> = arrayListOf()
        calleeSaveRegisters.addAll(getsRegs())
        return calleeSaveRegisters
    }

    fun getaRegs(): List<Int> {
        return (Registers.a0..Registers.a7).toList()
    }

    fun getsRegs(): List<Int> {
        val sRegs: MutableList<Int> = (Registers.s0..Registers.s1).toMutableList()
        sRegs.addAll(Registers.s2..Registers.s11)
        return sRegs
    }

    fun gettRegs(): List<Int> {
        val sRegs: MutableList<Int> = (Registers.t0..Registers.t2).toMutableList()
        sRegs.addAll(Registers.t3..Registers.t6)
        return sRegs
    }
}