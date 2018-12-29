package venusbackend.riscv.insts.integer.base.r

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.RTypeInstruction

val sllw = RTypeInstruction(
        name = "sllw",
        opcode = 0b0111011,
        funct3 = 0b001,
        funct7 = 0b0000000,
        eval16 = { a, b ->
            throw InstructionNotSupportedError("sllw is not supported on 16 bit systems!")
        },
        eval32 = { a, b ->
            throw InstructionNotSupportedError("sllw is not supported on 32 bit systems!")
        },
        eval64 = { a, b ->
            val shift = b.toInt() and 0b11111
            if (shift == 0) a.toInt().toLong() else (a.toInt() shl shift).toLong()
        },
        eval128 = { a, b ->
            val shift = b.toInt() and 0b11111
            if (shift == 0) a.toInt().toQuadWord() else (a.toInt() shl shift).toQuadWord()
        }
)