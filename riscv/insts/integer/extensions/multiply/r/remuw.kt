package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.base.RTypeInstruction

val remuw = RTypeInstruction(
        name = "remuw",
        opcode = 0b0111011,
        funct3 = 0b111,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            throw InstructionNotSupportedError("remuw is not supported on 16 bit systems!")
        },
        eval32 = { a, b ->
            throw InstructionNotSupportedError("remuw is not supported on 32 bit systems!")
        },
        eval64 = { a, b ->
            val x = a.toLong() shl 32 ushr 32
            val y = b.toLong() shl 32 ushr 32
            if (b.toInt() == 0) a.toInt().toLong()
            else (x % y).toInt().toLong()
        },
        eval128 = { a, b ->
            val x = a.toLong() shl 32 ushr 32
            val y = b.toLong() shl 32 ushr 32
            if (b.toInt() == 0) a.toInt().toQuadWord()
            else (x % y).toInt().toQuadWord()
        }
)
