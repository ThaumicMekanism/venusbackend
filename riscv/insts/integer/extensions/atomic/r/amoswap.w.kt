package venusbackend.riscv.insts.integer.extensions.atomic.r

import venusbackend.riscv.insts.dsl.AMORTypeInstruction

val amoswapweval32 = { data: Int, vrs2: Int -> vrs2 }

val amoswapw = AMORTypeInstruction(
        name = "amoswap.w",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00001,
        rl = 0b0,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoswapweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoswapwaq = AMORTypeInstruction(
        name = "amoswap.w.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00001,
        rl = 0b0,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoswapweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoswapwrl = AMORTypeInstruction(
        name = "amoswap.w.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00001,
        rl = 0b1,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoswapweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoswapwaqrl = AMORTypeInstruction(
        name = "amoswap.w.aq.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00001,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoswapweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoswapwrlaq = AMORTypeInstruction(
        name = "amoswap.w.rl.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00001,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoswapweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)