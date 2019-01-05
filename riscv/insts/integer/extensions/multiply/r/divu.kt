package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.numbers.QuadWord
import venusbackend.numbers.toDoubleQuadWord
import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.RTypeInstruction

val divu = RTypeInstruction(
        name = "divu",
        opcode = 0b0110011,
        funct3 = 0b101,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            val x = a.toInt() shl 16 ushr 16
            val y = b.toInt() shl 16 ushr 16
            if (y == (0L).toInt()) Short.MAX_VALUE
            else (x / y).toShort()
        },
        eval32 = { a, b ->
            val x = a.toLong() shl 32 ushr 32
            val y = b.toLong() shl 32 ushr 32
            if (y == 0L) Int.MAX_VALUE
            else (x / y).toInt()
        },
        eval64 = { a, b ->
            val x = a.toQuadWord() shl 64 ushr 64
            val y = b.toQuadWord() shl 64 ushr 64
            if (y == QuadWord()) Long.MAX_VALUE
            else (x / y).toLong()
        },
        /*FIXME 128 need to be able to convert to larger things that LONG*/
        eval128 = { a, b ->
            val x = a.toDoubleQuadWord() shl 128 ushr 128
            val y = b.toDoubleQuadWord() shl 128 ushr 128
            if (y == 0.toDoubleQuadWord()) QuadWord.MAX_VALUE
            else (x / y).toQuadWord()
        }
)
