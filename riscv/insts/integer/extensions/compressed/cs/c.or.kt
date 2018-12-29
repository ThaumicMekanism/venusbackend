package venusbackend.riscv.insts.integer.extensions.compressed.cs

import venusbackend.riscv.insts.dsl.types.compressed.CSTypeInstruction

val cor = CSTypeInstruction(
        name = "c.or",
        opcode2 = 0b01,
        funct = 0b11,
        funct6 = 0b100011,
        eval16 = { a, b -> (a.toInt() or b.toInt()).toShort() },
        eval32 = { a, b -> a or b },
        eval64 = { a, b -> a or b },
        eval128 = { a, b -> a or b }
)