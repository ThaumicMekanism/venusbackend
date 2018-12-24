package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.dsl.RTypeInstruction

val and = RTypeInstruction(
        name = "and",
        opcode = 0b0110011,
        funct3 = 0b111,
        funct7 = 0b0000000,
        eval16 = { a, b -> (a.toInt() and b.toInt()).toShort() },
        eval32 = { a, b -> a and b },
        eval64 = { a, b -> a and b },
        eval128 = { a, b -> a and b }
)
