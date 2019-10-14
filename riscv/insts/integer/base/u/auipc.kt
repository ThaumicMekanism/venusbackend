package venusbackend.riscv.insts.integer.base.u

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.base.UTypeInstruction

val auipc = UTypeInstruction(
        name = "auipc",
        opcode = 0b0010111,
        impl16 = { mcode, sim ->
            throw InstructionNotSupportedError("auipc is not supported on 16 bit systems!")
        },
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
        impl128 = { mcode, sim ->
            val offset = (mcode[InstructionField.IMM_31_12].toInt() shl 12).toQuadWord()
            sim.setReg(mcode[InstructionField.RD].toInt(), sim.getPC().toQuadWord() + offset)
            sim.incrementPC(mcode.length)
        }
)
