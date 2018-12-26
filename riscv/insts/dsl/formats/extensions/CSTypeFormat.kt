package venusbackend.riscv.insts.dsl.formats.extensions

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat

class CSTypeFormat(opcode2: Int, funct: Int, funct6: Int) : InstructionFormat(2, listOf(
        FieldEqual(InstructionField.OP2, opcode2),
        FieldEqual(InstructionField.FUNCT, funct),
        FieldEqual(InstructionField.FUNCT6, funct6)
))
