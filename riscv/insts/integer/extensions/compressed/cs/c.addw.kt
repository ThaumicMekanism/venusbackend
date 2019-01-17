package venusbackend.riscv.insts.integer.extensions.compressed.cs

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.compressed.CSTypeInstruction

val caddw = CSTypeInstruction(
        name = "c.addw",
        opcode2 = 0b01,
        funct = 0b01,
        funct6 = 0b100111,
        eval16 = { a, b -> throw InstructionNotSupportedError("c.addw is not supported on 16 bit systems!") },
        eval32 = { a, b -> throw InstructionNotSupportedError("c.addw is not supported on 32 bit systems!") },
        eval64 = { a, b -> (a.toInt() + b.toInt()).toLong() },
        eval128 = { a, b -> (a.toInt() + b.toInt()).toQuadWord() }
)