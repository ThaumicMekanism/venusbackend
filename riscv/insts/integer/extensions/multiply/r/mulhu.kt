package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.numbers.toDoubleQuadWord
import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.RTypeInstruction

val mulhu = RTypeInstruction(
        name = "mulhu",
        opcode = 0b0110011,
        funct3 = 0b011,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            val x = (a.toInt() shl 16) ushr 16
            val y = (b.toInt() shl 16) ushr 16
            ((x * y) ushr 16).toShort()
        },
        eval32 = { a, b ->
            val x = (a.toLong() shl 32) ushr 32
            val y = (b.toLong() shl 32) ushr 32
            ((x * y) ushr 32).toInt()
        },
        eval64 = { a, b ->
            val x = (a.toQuadWord() shl 32) ushr 32
            val y = (b.toQuadWord() shl 32) ushr 32
            ((x * y) ushr 32).toLong()
        },
        eval128 = { a, b ->
            val x = (a.toDoubleQuadWord() shl 32) ushr 32
            val y = (b.toDoubleQuadWord() shl 32) ushr 32
            ((x * y) ushr 32).toQuadWord()
        }
)
