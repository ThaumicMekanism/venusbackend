package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.dsl.types.RTypeInstruction
import venusbackend.shl
import kotlin.experimental.and

val sll = RTypeInstruction(
        name = "sll",
        opcode = 0b0110011,
        funct3 = 0b001,
        funct7 = 0b0000000,
        eval16 = { a, b ->
            val shift = b and 0b11111
            if (shift == 0.toShort()) a else (a shl shift).toShort()
        },
        eval32 = { a, b ->
            val shift = b and 0b11111
            if (shift == 0) a else a shl shift
        },
        eval64 = { a, b ->
            val shift = b.toInt() and 0b111111
            if (shift == 0) a else a shl shift
        },
        eval128 = { a, b ->
            val shift = b.toInt() and 0b1111111
            if (shift == 0) a else a shl shift
        }
)
