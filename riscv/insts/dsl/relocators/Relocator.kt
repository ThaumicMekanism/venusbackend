package venusbackend.riscv.insts.dsl.relocators

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.MachineCode

interface Relocator32 {
    operator fun invoke(mcode: MachineCode, pc: Int, target: Int, dbg: DebugInfo)
}

interface Relocator64 {
    operator fun invoke(mcode: MachineCode, pc: Long, target: Long, dbg: DebugInfo)
}

class Relocator(private val relocator32: Relocator32, private val relocator64: Relocator64) {
    operator fun invoke(mcode: MachineCode, pc: Number, target: Number, is64: Boolean = false, dbg: DebugInfo) {
        if (is64) relocator64(mcode, pc.toLong(), target.toLong(), dbg)
        else relocator32(mcode, pc.toInt(), target.toInt(), dbg)
    }
}
