package venusbackend.riscv.insts.dsl.formats.base

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat

class RTypeFormat(opcode: Int, funct3: Int, funct7: Int) : InstructionFormat(4, listOf(
        FieldEqual(InstructionField.OPCODE, opcode),
        FieldEqual(InstructionField.FUNCT3, funct3),
        FieldEqual(InstructionField.FUNCT7, funct7)
))
