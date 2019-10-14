package venusbackend.riscv.insts.dsl.formats.extensions.atomic

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat

class AMORTypeFormat(opcode: Int, funct3: Int, funct5: Int, aq: Int, rl: Int) : InstructionFormat(4, listOf(
        FieldEqual(InstructionField.OPCODE, opcode),
        FieldEqual(InstructionField.FUNCT3, funct3),
        FieldEqual(InstructionField.FUNCT5, funct5),
        FieldEqual(InstructionField.AQ, aq),
        FieldEqual(InstructionField.RL, rl)
))
