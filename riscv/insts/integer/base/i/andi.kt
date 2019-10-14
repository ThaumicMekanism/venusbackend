package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.dsl.types.base.ITypeInstruction

val andi = ITypeInstruction(
        name = "andi",
        opcode = 0b0010011,
        funct3 = 0b111,
        eval16 = { a, b -> (a.toInt() and b.toInt()).toShort() },
        eval32 = { a, b -> a and b },
        eval64 = { a, b -> a and b },
        eval128 = { a, b -> a and b }
)
