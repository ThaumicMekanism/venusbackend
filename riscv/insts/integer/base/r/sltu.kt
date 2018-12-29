package venusbackend.riscv.insts.integer.base.r

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.RTypeInstruction
import venusbackend.riscv.insts.dsl.compareUnsigned
import venusbackend.riscv.insts.dsl.compareUnsignedLong
import venusbackend.riscv.insts.dsl.compareUnsignedQuadWord
import venusbackend.riscv.insts.dsl.compareUnsignedShort

val sltu = RTypeInstruction(
        name = "sltu",
        opcode = 0b0110011,
        funct3 = 0b011,
        funct7 = 0b0000000,
        eval16 = { a, b -> if (compareUnsignedShort(a, b) < 0) 1 else 0 },
        eval32 = { a, b -> if (compareUnsigned(a, b) < 0) 1 else 0 },
        eval64 = { a, b -> if (compareUnsignedLong(a, b) < 0) 1 else 0 },
        eval128 = { a, b -> if (compareUnsignedQuadWord(a, b) < 0) 1.toQuadWord() else 0.toQuadWord() }
)
