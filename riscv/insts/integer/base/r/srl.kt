package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.dsl.types.base.RTypeInstruction
import venusbackend.ushr
import kotlin.experimental.and

val srl = RTypeInstruction(
        name = "srl",
        opcode = 0b0110011,
        funct3 = 0b101,
        funct7 = 0b0000000,
        eval16 = { a, b ->
            val shift = b and 0b1111
            if (shift == 0.toShort()) a else (a ushr shift).toShort()
        },
        eval32 = { a, b ->
            val shift = b and 0b11111
            if (shift == 0) a else a ushr shift
        },
        eval64 = { a, b ->
            val shift = b.toInt() and 0b111111
            if (shift == 0) a else a ushr shift
        },
        eval128 = { a, b ->
            val shift = b.toInt() and 0b1111111
            if (shift == 0) a else a ushr shift
        }
)
