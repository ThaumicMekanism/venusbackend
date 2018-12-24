package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.dsl.RTypeInstruction

val sra = RTypeInstruction(
        name = "sra",
        opcode = 0b0110011,
        funct3 = 0b101,
        funct7 = 0b0100000,
//        eval16 = { a, b ->
//            val shift = b and 0b11111
//            if (shift == 0) a else a shr shift
//        },
        eval32 = { a, b ->
            val shift = b and 0b11111
            if (shift == 0) a else a shr shift
        },
        eval64 = { a, b ->
            val shift = b.toInt() and 0b111111
            if (shift == 0) a else a shr shift
        }
//        eval128 = { a, b ->
//            val shift = b.toInt() and 0b111111
//            if (shift == 0) a else a shr shift
//        }
)
