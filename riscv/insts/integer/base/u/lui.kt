package venusbackend.riscv.insts.integer.base.u

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.base.UTypeInstruction

val lui = UTypeInstruction(
        name = "lui",
        opcode = 0b0110111,
        impl16 = { mcode, sim ->
            throw InstructionNotSupportedError("lui is not supported on 16 bit systems!")
        },
        impl32 = { mcode, sim ->
            val imm = mcode[InstructionField.IMM_31_12].toInt() shl 12
            sim.setReg(mcode[InstructionField.RD].toInt(), imm)
            sim.incrementPC(mcode.length)
        },
        impl64 = { mcode, sim ->
            val imm = (mcode[InstructionField.IMM_31_12].toInt() shl 12).toLong()
            sim.setReg(mcode[InstructionField.RD].toInt(), imm)
            sim.incrementPC(mcode.length)
        },
        impl128 = { mcode, sim ->
            val imm = (mcode[InstructionField.IMM_31_12].toInt() shl 12).toQuadWord()
            sim.setReg(mcode[InstructionField.RD].toInt(), imm)
            sim.incrementPC(mcode.length)
        }
)
