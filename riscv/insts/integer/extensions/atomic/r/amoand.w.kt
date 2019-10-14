package venusbackend.riscv.insts.integer.extensions.atomic.r

import venusbackend.riscv.insts.dsl.types.extensions.atomic.AMORTypeInstruction

val amoandweval32 = { data: Int, vrs2: Int -> data and vrs2 }

val amoandw = AMORTypeInstruction(
        name = "amoand.w",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b01100,
        rl = 0b0,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoandweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoandwaq = AMORTypeInstruction(
        name = "amoand.w.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b01100,
        rl = 0b0,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoandweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoandwrl = AMORTypeInstruction(
        name = "amoand.w.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b01100,
        rl = 0b1,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoandweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoandwaqrl = AMORTypeInstruction(
        name = "amoand.w.aq.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b01100,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoandweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amoandwrlaq = AMORTypeInstruction(
        name = "amoand.w.rl.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b01100,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amoandweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)