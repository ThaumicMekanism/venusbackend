package venusbackend.riscv.insts.integer.extensions.compressed.cs

import venusbackend.riscv.insts.dsl.CSTypeInstruction

val cxor = CSTypeInstruction(
        name = "c.xor",
        opcode2 = 0b01,
        funct = 0b11,
        funct6 = 0b100011,
        eval16 = { a, b -> (a.toInt() xor b.toInt()).toShort() },
        eval32 = { a, b -> a xor b },
        eval64 = { a, b -> a xor b },
        eval128 = { a, b -> a xor b }
)