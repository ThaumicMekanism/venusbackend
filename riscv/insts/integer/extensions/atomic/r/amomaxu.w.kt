package venusbackend.riscv.insts.integer.extensions.atomic.r

import venusbackend.riscv.insts.dsl.AMORTypeInstruction

/*todo test if this 'unsigned' conversion works*/
val amomaxuweval32 = { data: Int, vrs2: Int -> maxOf(data xor Int.MIN_VALUE, vrs2 xor Int.MIN_VALUE) }

val amomaxuw = AMORTypeInstruction(
        name = "amomaxu.w",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b11100,
        rl = 0b0,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amomaxuweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amomaxuwaq = AMORTypeInstruction(
        name = "amomaxu.w.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b11100,
        rl = 0b0,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amomaxuweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amomaxuwrl = AMORTypeInstruction(
        name = "amomaxu.w.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b11100,
        rl = 0b1,
        aq = 0b0,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amomaxuweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amomaxuwaqrl = AMORTypeInstruction(
        name = "amomaxu.w.aq.rl",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b11100,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amomaxuweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)

val amomaxuwrlaq = AMORTypeInstruction(
        name = "amomaxu.w.rl.aq",
        opcode = 0b0101111,
        funct3 = 0b010,
        funct5 = 0b11100,
        rl = 0b1,
        aq = 0b1,
        // eval16 = { a, b -> (a + b).toShort() },
        eval32 = amomaxuweval32
        // eval64 = { a, b -> a + b },
        // eval128 = { a, b -> a + b }
)