package venusbackend.riscv.insts.dsl.formats

import venusbackend.riscv.InstructionField

open class OpcodeCFunct3Format(opcode: Int, cfunct3: Int) : InstructionFormat(4, listOf(
        FieldEqual(InstructionField.OPCODE, opcode),
        FieldEqual(InstructionField.CFUNCT3, cfunct3)
))
