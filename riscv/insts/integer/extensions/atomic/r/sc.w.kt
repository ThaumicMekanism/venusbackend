package venusbackend.riscv.insts.integer.extensions.atomic.r

import venusbackend.riscv.insts.dsl.types.extensions.atomic.AMORTypeInstruction
/*@FIXME*/
val scweval32 = { data: Int, vrs2: Int -> data xor vrs2 }

val scw = AMORTypeInstruction(
        name = "sc.w",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00011,
        rl = 0b0,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = scweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val scwaq = AMORTypeInstruction(
        name = "sc.w.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00011,
        rl = 0b0,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = scweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val scwrl = AMORTypeInstruction(
        name = "sc.w.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00011,
        rl = 0b1,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = scweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val scwaqrl = AMORTypeInstruction(
        name = "sc.w.aq.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00011,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = scweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val scwrlaq = AMORTypeInstruction(
        name = "sc.w.rl.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b00011,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = scweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)