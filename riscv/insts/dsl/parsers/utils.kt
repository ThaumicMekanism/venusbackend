package venusbackend.riscv.insts.dsl.parsers

import venusbackend.assembler.AssemblerError
import venusbackend.assembler.DebugInfo
import venusbackend.simulator.SpecialRegisters

internal fun checkArgsLength(argsSize: Int, required: Int, dbg: DebugInfo) {
    if (argsSize != required)
        throw AssemblerError("got $argsSize arguments but expected $required", dbg)
}

/**
 * Converts a register name to its ID.
 *
 * Accepts ABI names (e.g., ra, fp) and RISC-V names (e.g., x1, x8)
 *
 * @param reg the name of the register
 * @return the ID of the register
 *
 * @throws AssemblerError if given an invalid register
 */
internal fun regNameToNumber(reg: String, integer: Boolean = true, dbg: DebugInfo): Int {
    if (reg.startsWith("x")) {
        val ret = reg.drop(1).toInt()
        if (ret in 0..31) {
            if (!integer) throw AssemblerError("Register $reg is not a floating point register", dbg)
            return ret
        }
        throw AssemblerError("register $reg not recognized", dbg)
    }
    if (reg.matches(Regex("f\\d{1,2}"))) {
        val ret = reg.drop(1).toInt()
        if (ret in 0..31) {
            if (integer) throw AssemblerError("Register $reg is not an integer register", dbg)
            return ret
        }
        throw AssemblerError("register $reg not recognized", dbg)
    }
    val CSR = checkCSR(reg)
    if (CSR != null) {
        return CSR
    }
    try {
        if (integer) {
            return checkInteger(reg, dbg)
        } else {
            return checkFloating(reg, dbg)
        }
    } catch (e: AssemblerError) {
        if (integer) {
            checkFloating(reg, dbg)
            throw AssemblerError("Register $reg is not an integer register", dbg)
        } else {
            checkInteger(reg, dbg)
            throw AssemblerError("Register $reg is not a floating point register", dbg)
        }
    }
}

fun checkInteger(reg: String, dbg: DebugInfo): Int {
    return when (reg) {
        "zero" -> 0
        "ra" -> 1
        "sp" -> 2
        "gp" -> 3
        "tp" -> 4
        "t0" -> 5
        "t1" -> 6
        "t2" -> 7
        "s0", "fp" -> 8
        "s1" -> 9
        "a0" -> 10
        "a1" -> 11
        "a2" -> 12
        "a3" -> 13
        "a4" -> 14
        "a5" -> 15
        "a6" -> 16
        "a7" -> 17
        "s2" -> 18
        "s3" -> 19
        "s4" -> 20
        "s5" -> 21
        "s6" -> 22
        "s7" -> 23
        "s8" -> 24
        "s9" -> 25
        "s10" -> 26
        "s11" -> 27
        "t3" -> 28
        "t4" -> 29
        "t5" -> 30
        "t6" -> 31
        else -> throw AssemblerError("register $reg not recognized", dbg)
    }
}

fun checkFloating(reg: String, dbg: DebugInfo): Int {
    return when (reg) {
        "ft0" -> 0
        "ft1" -> 1
        "ft2" -> 2
        "ft3" -> 3
        "ft4" -> 4
        "ft5" -> 5
        "ft6" -> 6
        "ft7" -> 7
        "fs0" -> 8
        "fs1" -> 9
        "fa0" -> 10
        "fa1" -> 11
        "fa2" -> 12
        "fa3" -> 13
        "fa4" -> 14
        "fa5" -> 15
        "fa6" -> 16
        "fa7" -> 17
        "fs2" -> 18
        "fs3" -> 19
        "fs4" -> 20
        "fs5" -> 21
        "fs6" -> 22
        "fs7" -> 23
        "fs8" -> 24
        "fs9" -> 25
        "fs10" -> 26
        "fs11" -> 27
        "ft8" -> 28
        "ft9" -> 29
        "ft10" -> 30
        "ft11" -> 31
        else -> throw AssemblerError("register $reg not recognized", dbg)
    }
}
fun checkCSR(reg: String): Int? {
    /*
        TODO:
        -> optimize to make more flexible (loop over instead of hard code enums)
     */
    return when(reg) {
        SpecialRegisters.MSTATUS.regName -> SpecialRegisters.MSTATUS.address
        SpecialRegisters.MIE.regName -> SpecialRegisters.MIE.address
        SpecialRegisters.MTVEC.regName -> SpecialRegisters.MTVEC.address
        SpecialRegisters.MSCRATCH.regName -> SpecialRegisters.MSCRATCH.address
        SpecialRegisters.MEPC.regName -> SpecialRegisters.MEPC.address
        SpecialRegisters.MCAUSE.regName -> SpecialRegisters.MCAUSE.address
        SpecialRegisters.MTVAL.regName -> SpecialRegisters.MTVAL.address
        SpecialRegisters.MIP.regName -> SpecialRegisters.MIP.address
        SpecialRegisters.MTIME.regName -> SpecialRegisters.MTIME.address
        SpecialRegisters.MTIMECMP.regName -> SpecialRegisters.MTIMECMP.address
        else -> null
    }
}
