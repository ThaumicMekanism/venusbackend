package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.dsl.ITypeInstruction

val slti = ITypeInstruction(
        name = "slti",
        opcode = 0b0010011,
        funct3 = 0b010,
        eval16 = { a, b -> if (a < b) 1 else 0 },
        eval32 = { a, b -> if (a < b) 1 else 0 },
        eval64 = { a, b -> if (a < b) 1 else 0 },
        eval128 = { a, b -> if (a < b) 1 else 0 }
)
