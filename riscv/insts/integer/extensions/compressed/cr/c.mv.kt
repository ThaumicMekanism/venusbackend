package venusbackend.riscv.insts.integer.extensions.compressed.cr

import venusbackend.riscv.insts.dsl.types.extensions.compressed.CRTypeInstruction

val cmv = CRTypeInstruction(
        name = "c.mv",
        opcode2 = 0b10,
        funct4 = 0b1000,
        eval32 = { _, b ->
            b
        }
)
