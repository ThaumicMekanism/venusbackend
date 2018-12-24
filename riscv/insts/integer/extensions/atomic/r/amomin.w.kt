package venusbackend.riscv.insts.integer.extensions.atomic.r

import venusbackend.riscv.insts.dsl.AMORTypeInstruction

val amominweval32 = { data: Int, vrs2: Int -> minOf(data, vrs2) }

val amominw = AMORTypeInstruction(
        name = "amomin.w",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b10000,
        rl = 0b0,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amominweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amominwaq = AMORTypeInstruction(
        name = "amomin.w.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b10000,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amominweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amominwrl = AMORTypeInstruction(
        name = "amomin.w.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b10000,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = { data, vrs2 -> minOf(data, vrs2) }
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amominwaqrl = AMORTypeInstruction(
        name = "amomin.w.aq.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b10000,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amominweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amominwrlaq = AMORTypeInstruction(
        name = "amomin.w.rl.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b10000,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amominweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)