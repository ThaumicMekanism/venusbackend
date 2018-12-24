package venusbackend.riscv.insts.dsl.formats

import venusbackend.riscv.InstructionField

open class OpcodeFormat(opcode: Int) : InstructionFormat(4, listOf(
        FieldEqual(InstructionField.OPCODE, opcode)
))
