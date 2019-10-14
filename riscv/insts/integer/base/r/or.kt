package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.dsl.types.base.RTypeInstruction

val or = RTypeInstruction(
        name = "or",
        opcode = 0b0110011,
        funct3 = 0b110,
        funct7 = 0b0000000,
        eval16 = { a, b -> (a.toInt() or b.toInt()).toShort() },
        eval32 = { a, b -> a or b },
        eval64 = { a, b -> a or b },
        eval128 = { a, b -> a or b }
)
