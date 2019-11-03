package venusbackend.riscv.insts.dsl.formats

import venusbackend.riscv.InstructionField

open class OpcodeCFunct3Format(opcode: Int, cfunct3: Int, regComp: Array<FieldEqual>) : InstructionFormat(2, arrayOf(
        arrayOf(
            FieldEqual(InstructionField.OPCODE, opcode),
            FieldEqual(InstructionField.CFUNCT3, cfunct3)
        ),
        regComp
    ).flatten()
)
