package venusbackend.riscv.insts.dsl.relocators

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode

private object PCRelLoStoreRelocator32 : Relocator32 {
    override operator fun invoke(mcode: MachineCode, pc: Int, target: Int) {
        val offset = target - (pc - 4)
        mcode[InstructionField.IMM_4_0] = offset
        mcode[InstructionField.IMM_11_5] = offset shr 5
    }
}

val PCRelLoStoreRelocator = Relocator(PCRelLoStoreRelocator32, NoRelocator64)
