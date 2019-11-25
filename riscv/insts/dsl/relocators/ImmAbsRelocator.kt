package venusbackend.riscv.insts.dsl.relocators

import venusbackend.assembler.AssemblerError
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode

/** Set the immediate field of an I-type instruction MCODE to TARGET.
 *  The value of PC is unused. */
private object ImmAbsRelocator32 : Relocator32 {
    override operator fun invoke(mcode: MachineCode, pc: Int, target: Int) {
        if (target in -2048..2047) {
            mcode[InstructionField.IMM_11_0] = target
        } else {
            throw AssemblerError("immediate value out of range: $target")
        }
    }
}

val ImmAbsRelocator = Relocator(ImmAbsRelocator32, NoRelocator64)