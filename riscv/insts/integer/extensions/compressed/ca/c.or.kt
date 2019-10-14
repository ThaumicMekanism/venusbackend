package venusbackend.riscv.insts.integer.extensions.compressed.ca

import venusbackend.riscv.insts.dsl.types.extensions.compressed.CATypeInstruction

val cor = CATypeInstruction(
        name = "c.or",
        opcode2 = 0b01,
        funct2 = 0b10,
        funct6 = 0b100011,
        eval32 = { a, b ->
            a xor b
        }
)