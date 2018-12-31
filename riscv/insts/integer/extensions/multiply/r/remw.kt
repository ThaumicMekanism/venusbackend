package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.RTypeInstruction

val remw = RTypeInstruction(
        name = "remw",
        opcode = 0b0111011,
        funct3 = 0b110,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            throw InstructionNotSupportedError("remw is not supported on 16 bit systems!")
        },
        eval32 = { a, b ->
            throw InstructionNotSupportedError("remw is not supported on 32 bit systems!")
        },
        eval64 = { a, b ->
            if (b.toInt() == 0) a
            else if (a.toInt() == Int.MIN_VALUE && b.toInt() == -1) 0.toLong()
            else (a.toInt() % b.toInt()).toLong()
        },
        eval128 = { a, b ->
            if (b.toInt() == 0) a.toInt().toQuadWord()
            else if (a.toInt() == Int.MIN_VALUE && b.toInt() == -1) 0.toQuadWord()
            else (a.toInt() % b.toInt()).toQuadWord()
        }
)
