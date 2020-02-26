package venusbackend.riscv.insts.dsl.relocators

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode

private object PCRelLoRelocator32 : Relocator32 {
    override operator fun invoke(mcode: MachineCode, pc: Int, target: Int, dbg: DebugInfo) {
        mcode[InstructionField.IMM_11_0] = target - (pc - 4)
    }
}

val PCRelLoRelocator = Relocator(PCRelLoRelocator32, NoRelocator64)
