package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.riscv.insts.dsl.RTypeInstruction

val mulhsu = RTypeInstruction(
        name = "mulhsu",
        opcode = 0b0110011,
        funct3 = 0b010,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            val x = a.toInt()
            val y = (b.toInt() shl 16) ushr 16
            ((x * y) ushr 16).toShort()
        },
        eval32 = { a, b ->
            val x = a.toLong()
            val y = (b.toLong() shl 32) ushr 32
            ((x * y) ushr 32).toInt()
        }
//        eval64 = { a, b ->
//            val x = a.toLong()
//            val y = (b.toLong() shl 32) ushr 32
//            ((x * y) ushr 32).toInt()
//        }
//        eval128 = { a, b ->
//            val x = a.toLong()
//            val y = (b.toLong() shl 32) ushr 32
//            ((x * y) ushr 32).toInt()
//        }
)
