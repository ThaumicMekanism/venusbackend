package venusbackend.riscv.insts.integer.base.u

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.UTypeInstruction
import venusbackend.riscv.insts.dsl.impls.NoImplementation

val auipc = UTypeInstruction(
        name = "auipc",
        opcode = 0b0010111,
        impl16 = NoImplementation::invoke,
        impl32 = { mcode, sim ->
            val offset = mcode[InstructionField.IMM_31_12].toInt() shl 12
            sim.setReg(mcode[InstructionField.RD].toInt(), sim.getPC().toInt() + offset)
            sim.incrementPC(mcode.length)
        },
        impl64 = { mcode, sim ->
            val offset = (mcode[InstructionField.IMM_31_12].toInt() shl 12).toLong()
            sim.setReg(mcode[InstructionField.RD].toInt(), sim.getPC().toLong() + offset)
            sim.incrementPC(mcode.length)
        },
        impl128 = NoImplementation::invoke
)
