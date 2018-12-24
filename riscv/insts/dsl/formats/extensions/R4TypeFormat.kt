package venusbackend.riscv.insts.dsl.formats.extensions

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat

/**
 * Created by thaum on 8/6/2018.
 */
class R4TypeFormat(opcode: Int, funct2: Int) : InstructionFormat(4, listOf(
        FieldEqual(InstructionField.OPCODE, opcode),
        // FieldEqual(InstructionField.FUNCT3, funct3),
        FieldEqual(InstructionField.FUNCT2, funct2)
))