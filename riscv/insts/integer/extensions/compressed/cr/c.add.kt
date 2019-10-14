package venusbackend.riscv.insts.integer.extensions.compressed.cr

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.types.extensions.compressed.CRTypeInstruction

val cadd = CRTypeInstruction(
        name = "c.add",
        opcode2 = 0b10,
        funct4 = 0b1001,
        regComp = arrayOf(
                FieldEqual(InstructionField.RD, 0, true),
                FieldEqual(InstructionField.CRS2, 0, true)
        ),
        eval32 = { a, b ->
            a + b
        }
)