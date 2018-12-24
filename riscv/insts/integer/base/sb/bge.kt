package venusbackend.riscv.insts.integer.base.sb

import venusbackend.riscv.insts.dsl.BTypeInstruction

val bge = BTypeInstruction(
        name = "bge",
        opcode = 0b1100011,
        funct3 = 0b101,
        cond16 = { a, b -> a >= b },
        cond32 = { a, b -> a >= b },
        cond64 = { a, b -> a >= b },
        cond128 = { a, b -> a >= b }
)
