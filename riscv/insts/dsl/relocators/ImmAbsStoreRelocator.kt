package venusbackend.riscv.insts.dsl.relocators

import venusbackend.assembler.AssemblerError
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode

private object ImmAbsStoreRelocator32 : Relocator32 {
    override operator fun invoke(mcode: MachineCode, pc: Int, target: Int) {
        if (target in -2048..2047) {
            mcode[InstructionField.IMM_4_0] = target
            mcode[InstructionField.IMM_11_5] = target shr 5
        } else {
            throw AssemblerError("immediate value out of range: $target")
        }
    }
}

val ImmAbsStoreRelocator = Relocator(ImmAbsStoreRelocator32, NoRelocator64)