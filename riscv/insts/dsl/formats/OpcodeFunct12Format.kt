package venusbackend.riscv.insts.dsl.formats

import venusbackend.riscv.InstructionField

open class OpcodeFunct12Format(opcode: Int, funct12: Int): InstructionFormat(4, listOf(
        FieldEqual(InstructionField.OPCODE, opcode),
        FieldEqual(InstructionField.FUNCT12, funct12)
))
