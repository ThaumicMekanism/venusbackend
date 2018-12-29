package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.ShiftImmediateInstruction

val srli = ShiftImmediateInstruction(
        name = "srli",
        funct3 = 0b101,
        funct7 = 0b0000000,
        eval16 = { a, b -> if (b == 0.toShort()) a else ((a.toInt() ushr b.toInt()).toShort()) },
        eval32 = { a, b -> if (b == 0) a else (a ushr b) },
        eval64 = { a, b -> if (b == 0.toLong()) a else (a ushr b.toInt()) },
        eval128 = { a, b -> if (b == 0.toQuadWord()) a else (a ushr b.toInt()) }
)
