package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.ShiftImmediateInstruction

val slli = ShiftImmediateInstruction(
        name = "slli",
        funct3 = 0b001,
        funct7 = 0b0000000,
        eval16 = { a, b -> if (b == 0.toShort()) a else ((a.toInt() shl b.toInt()).toShort()) },
        eval32 = { a, b -> if (b == 0) a else (a shl b) },
        eval64 = { a, b -> if (b == 0.toLong()) a else (a shl b.toInt()) },
        eval128 = { a, b -> if (b == 0.toQuadWord()) a else (a shl b.toInt()) }
)
