package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.riscv.insts.dsl.RTypeInstruction

val divu = RTypeInstruction(
        name = "divu",
        opcode = 0b0110011,
        funct3 = 0b101,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            val x = a.toInt() shl 16 ushr 16
            val y = b.toInt() shl 16 ushr 16
            if (y == (0L).toInt()) a
            else (x / y).toShort()
        },
        eval32 = { a, b ->
            val x = a.toLong() shl 32 ushr 32
            val y = b.toLong() shl 32 ushr 32
            if (y == 0L) a
            else (x / y).toInt()
        },
        /*FIXME 64 and 128 need to be able to convert to larger things that LONG*/
        eval64 = { a, b ->
            val x = a shl 64 ushr 64
            val y = b shl 64 ushr 64
            if (y == 0L) a
            else (x / y)
        },
        eval128 = { a, b ->
            val x = a shl 128 ushr 128
            val y = b shl 128 ushr 128
            if (y == 0L) a
            else (x / y)
        }
)
