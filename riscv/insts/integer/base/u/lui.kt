package venusbackend.riscv.insts.integer.base.u

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.UTypeInstruction

val lui = UTypeInstruction(
        name = "lui",
        opcode = 0b0110111,
//        impl16 = NoImplementation,
        impl32 = { mcode, sim ->
            val imm = mcode[InstructionField.IMM_31_12].toInt() shl 12
            sim.setReg(mcode[InstructionField.RD].toInt(), imm)
            sim.incrementPC(mcode.length)
        }
//        impl64 = NoImplementation,
//        impl128 = NoImplementation
)
