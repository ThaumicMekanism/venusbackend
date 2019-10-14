package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.numbers.QuadWord
import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.base.RTypeInstruction

val div = RTypeInstruction(
        name = "div",
        opcode = 0b0110011,
        funct3 = 0b100,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            if (b == 0.toShort()) -1
            else if (a == Short.MIN_VALUE && b == (-1).toShort()) a
            else (a / b).toShort()
        },
        eval32 = { a, b ->
            if (b == 0) -1
            else if (a == Int.MIN_VALUE && b == -1) a
            else a / b
        },
        eval64 = { a, b ->
            if (b == 0.toLong()) -1
            else if (a == Long.MIN_VALUE && b == (-1).toLong()) a
            else a / b
        },
        eval128 = { a, b ->
            if (b == 0.toQuadWord()) (-1).toQuadWord()
            else if (a == QuadWord.MIN_VALUE && b == (-1).toQuadWord()) a
            else a / b
        }
)
