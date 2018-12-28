package venusbackend.riscv.insts.integer.extensions.compressed.cs

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.CSTypeInstruction

val csubw = CSTypeInstruction(
        name = "c.subw",
        opcode2 = 0b01,
        funct = 0b11,
        funct6 = 0b100011,
        eval16 = { a, b -> throw InstructionNotSupportedError("addiw is not supported on 16 bit systems!") },
        eval32 = { a, b -> throw InstructionNotSupportedError("addiw is not supported on 32 bit systems!") },
        eval64 = { a, b -> (a.toInt() - b.toInt()).toULong().toLong() },
        eval128 = { a, b -> (a.toInt() - b.toInt()).toULong().toLong() }
)