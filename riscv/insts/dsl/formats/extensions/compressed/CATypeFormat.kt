package venusbackend.riscv.insts.dsl.formats.extensions.compressed

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat

class CATypeFormat(opcode2: Int, funct2: Int, funct6: Int) : InstructionFormat(2, listOf(
                FieldEqual(InstructionField.OP2, opcode2),
                FieldEqual(InstructionField.CFUNCT2, funct2),
                FieldEqual(InstructionField.FUNCT6, funct6)
        )
)
