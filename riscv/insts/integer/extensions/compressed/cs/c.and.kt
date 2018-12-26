package venusbackend.riscv.insts.integer.extensions.compressed.cs

import venusbackend.riscv.insts.dsl.CSTypeInstruction

val cand = CSTypeInstruction(
        name = "c.and",
        opcode2 = 0b01,
        funct = 0b11,
        funct6 = 0b100011,
        eval16 = { a, b -> (a.toInt() and b.toInt()).toShort() },
        eval32 = { a, b -> a and b },
        eval64 = { a, b -> a and b },
        eval128 = { a, b -> a and b }
)