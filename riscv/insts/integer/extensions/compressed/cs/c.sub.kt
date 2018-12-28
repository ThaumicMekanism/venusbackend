package venusbackend.riscv.insts.integer.extensions.compressed.cs

import venusbackend.riscv.insts.dsl.CSTypeInstruction

val csub = CSTypeInstruction(
        name = "c.sub",
        opcode2 = 0b01,
        funct = 0b11,
        funct6 = 0b100011,
        eval16 = { a, b -> (a.toInt() - b.toInt()).toShort() },
        eval32 = { a, b -> a - b },
        eval64 = { a, b -> a - b },
        eval128 = { a, b -> a - b }
)