package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.ITypeInstruction

val addiw = ITypeInstruction(
        name = "addiw",
        opcode = 0b0011011,
        funct3 = 0b000,
        eval16 = { a, b ->
            throw InstructionNotSupportedError("addiw is not supported on 16 bit systems!")
        },
        eval32 = { a, b ->
            throw InstructionNotSupportedError("addiw is not supported on 32 bit systems!")
        },
        eval64 = { a, b ->
            (a.toInt() + b.toInt()).toLong()
        },
        eval128 = { a, b ->
            (a.toInt() + b.toInt()).toQuadWord()
        }
)