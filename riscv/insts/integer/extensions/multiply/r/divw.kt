package venusbackend.riscv.insts.integer.extensions.multiply.r

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.RTypeInstruction

val divw = RTypeInstruction(
        name = "divw",
        opcode = 0b0111011,
        funct3 = 0b100,
        funct7 = 0b0000001,
        eval16 = { a, b ->
            throw InstructionNotSupportedError("divw is not supported on 16 bit systems!")
        },
        eval32 = { a, b ->
            throw InstructionNotSupportedError("divw is not supported on 32 bit systems!")
        },
        eval64 = { a, b ->
            if (b.toInt() == 0) -1
            else if (a.toInt() == Int.MIN_VALUE && b.toInt() == -1) a.toInt().toLong()
            else (a.toInt() / b.toInt()).toLong()
        },
        eval128 = { a, b ->
            if (b.toInt() == 0) (-1).toQuadWord()
            else if (a.toInt() == Int.MIN_VALUE && b.toInt() == -1) a.toInt().toQuadWord()
            else (a.toInt() / b.toInt()).toQuadWord()
        }
)