package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.riscv.insts.dsl.RTypeInstruction

val mul = RTypeInstruction(
        name = "mul",
        opcode = 0b0110011,
        funct3 = 0b000,
        funct7 = 0b0000001,
        eval16 = { a, b -> (a * b).toShort() },
        eval32 = { a, b -> a * b },
        eval64 = { a, b -> a * b },
        eval128 = { a, b -> a * b }
)
