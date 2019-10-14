package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.numbers.QuadWord
import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.base.RTypeInstruction

val rem = RTypeInstruction(
        name = "rem",
        opcode = 0b0110011,
        funct3 = 0b110,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            if (b == 0.toShort()) a
            else if (a == Short.MIN_VALUE && b == (-1).toShort()) 0
            else (a % b).toShort()
        },
        eval32 = { a, b ->
            if (b == 0) a
            else if (a == Int.MIN_VALUE && b == -1) 0
            else a % b
        },
        eval64 = { a, b ->
            if (b == 0.toLong()) a
            else if (a == Long.MIN_VALUE && b == (-1).toLong()) 0
            else a % b
        },
        eval128 = { a, b ->
            if (b == 0.toQuadWord()) a
            else if (a == QuadWord.MIN_VALUE && b == (-1).toQuadWord()) 0.toQuadWord()
            else a % b
        }
)
