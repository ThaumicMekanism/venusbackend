package venusbackend.riscv.insts.integer.extensions.atomic.r

import venusbackend.riscv.insts.dsl.AMORTypeInstruction

val amoaddweval32 = { data: Int, vrs2: Int -> data + vrs2 }

val amoaddw = AMORTypeInstruction(
        name = "amoadd.w",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00000,
        rl = 0b0,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoaddweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoaddwaq = AMORTypeInstruction(
        name = "amoadd.w.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00000,
        rl = 0b0,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoaddweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoaddwrl = AMORTypeInstruction(
        name = "amoadd.w.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00000,
        rl = 0b1,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoaddweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoaddwaqrl = AMORTypeInstruction(
        name = "amoadd.w.aq.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00000,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoaddweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoaddwrlaq = AMORTypeInstruction(
        name = "amoadd.w.rl.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00000,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoaddweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)