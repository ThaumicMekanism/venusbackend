package venusbackend.riscv.insts.dsl.formats.extensions.compressed

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat

class CRTypeFormat(opcode2: Int, funct4: Int, regComp: Array<FieldEqual>) : InstructionFormat(2, arrayOf(
        arrayOf(
            FieldEqual(InstructionField.OP2, opcode2),
            FieldEqual(InstructionField.FUNCT4, funct4)
        ),
        regComp
).flatten())
