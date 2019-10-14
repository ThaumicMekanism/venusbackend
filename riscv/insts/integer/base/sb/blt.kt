package venusbackend.riscv.insts.integer.base.sb

import venusbackend.riscv.insts.dsl.types.base.BTypeInstruction

val blt = BTypeInstruction(
        name = "blt",
        opcode = 0b1100011,
        funct3 = 0b100,
        cond16 = { a, b -> a < b },
        cond32 = { a, b -> a < b },
        cond64 = { a, b -> a < b },
        cond128 = { a, b -> a < b }
)
