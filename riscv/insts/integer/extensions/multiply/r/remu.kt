package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.numbers.QuadWord
import venusbackend.numbers.toDoubleQuadWord
import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.base.RTypeInstruction

val remu = RTypeInstruction(
        name = "remu",
        opcode = 0b0110011,
        funct3 = 0b111,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            val x = a.toInt() shl 16 ushr 16
            val y = b.toInt() shl 16 ushr 16
            if (b == 0.toShort()) a
            else (x % y).toShort()
        },
        eval32 = { a, b ->
            val x = a.toLong() shl 32 ushr 32
            val y = b.toLong() shl 32 ushr 32
            if (b == 0) a
            else (x % y).toInt()
        },
        eval64 = { a, b ->
            val x = a.toQuadWord() shl 32 ushr 32
            val y = b.toQuadWord() shl 32 ushr 32
            if (b == 0L) a
            else (x % y).toLong()
        },
        eval128 = { a, b ->
            val x = a.toDoubleQuadWord() shl 32 ushr 32
            val y = b.toDoubleQuadWord() shl 32 ushr 32
            if (b == QuadWord()) a
            else (x % y).toQuadWord()
        }
)
