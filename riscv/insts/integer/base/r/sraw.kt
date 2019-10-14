package venusbackend.riscv.insts.integer.base.r

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.base.RTypeInstruction

val sraw = RTypeInstruction(
        name = "sraw",
        opcode = 0b0111011,
        funct3 = 0b101,
        funct7 = 0b0100000,
        eval16 = { a, b ->
            throw InstructionNotSupportedError("sraw is not supported on 16 bit systems!")
        },
        eval32 = { a, b ->
            throw InstructionNotSupportedError("sraw is not supported on 32 bit systems!")
        },
        eval64 = { a, b ->
            val shift = b.toInt() and 0b11111
            if (shift == 0) a.toInt().toLong() else (a.toInt() shr shift).toLong()
        },
        eval128 = { a, b ->
            val shift = b.toInt() and 0b11111
            if (shift == 0) a.toInt().toQuadWord() else (a.toInt() shr shift).toQuadWord()
        }
)