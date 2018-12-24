package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.dsl.RTypeInstruction

val xor = RTypeInstruction(
        name = "xor",
        opcode = 0b0110011,
        funct3 = 0b100,
        funct7 = 0b0000000,
        eval16 = { a, b -> (a.toInt() xor b.toInt()).toShort() },
        eval32 = { a, b -> a xor b },
        eval64 = { a, b -> a xor b },
        eval128 = { a, b -> a xor b }
)
