package venusbackend.riscv.insts.dsl.formats.extensions.floating

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat

/**
 * Created by thaum on 8/6/2018.
 */
class FRTypeFormat(opcode: Int, funct7: Int) : InstructionFormat(4, listOf(
        FieldEqual(InstructionField.OPCODE, opcode),
        FieldEqual(InstructionField.FUNCT7, funct7)
))
