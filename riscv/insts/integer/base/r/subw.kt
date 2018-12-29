package venusbackend.riscv.insts.integer.base.r

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.RTypeInstruction

val subw = RTypeInstruction(
        name = "subw",
        opcode = 0b0111011,
        funct3 = 0b000,
        funct7 = 0b0100000,
        eval16 = { a, b ->
            throw InstructionNotSupportedError("subw is not supported on 16 bit systems!")
        },
        eval32 = { a, b ->
            throw InstructionNotSupportedError("subw is not supported on 32 bit systems!")
        },
        eval64 = { a, b ->
            (a.toInt() - b.toInt()).toLong()
        },
        eval128 = { a, b ->
            (a.toInt() - b.toInt()).toQuadWord()
        }
)
