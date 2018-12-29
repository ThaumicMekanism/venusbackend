package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.riscv.insts.dsl.types.RTypeInstruction

val mulh = RTypeInstruction(
        name = "mulh",
        opcode = 0b0110011,
        funct3 = 0b001,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            val x = a.toInt()
            val y = b.toInt()
            ((x * y) ushr 16).toShort()
        },
        eval32 = { a, b ->
            val x = a.toLong()
            val y = b.toLong()
            ((x * y) ushr 32).toInt()
        }
//        eval64 = { a, b ->
//            val x = a.toLong()
//            val y = b.toLong()
//            ((x * y) ushr 64).toInt()
//        }
//        eval128 = { a, b ->
//            val x = a.toLong()
//            val y = b.toLong()
//            ((x * y) ushr 128).toInt()
//        }
)
