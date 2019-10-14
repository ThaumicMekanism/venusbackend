package venusbackend.riscv.insts.integer.extensions.compressed.ca

import venusbackend.riscv.insts.dsl.types.extensions.compressed.CATypeInstruction

val csub = CATypeInstruction(
        name = "c.sub",
        opcode2 = 0b01,
        funct2 = 0b00,
        funct6 = 0b100011,
        eval32 = { a, b ->
            a - b
        }
)