package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.ITypeInstruction
import venusbackend.riscv.insts.dsl.compareUnsigned
import venusbackend.riscv.insts.dsl.compareUnsignedLong
import venusbackend.riscv.insts.dsl.compareUnsignedQuadWord
import venusbackend.riscv.insts.dsl.compareUnsignedShort

val sltiu = ITypeInstruction(
        name = "sltiu",
        opcode = 0b0010011,
        funct3 = 0b011,
        eval16 = { a, b -> if (compareUnsignedShort(a, b) < 0) 1 else 0 },
        eval32 = { a, b -> if (compareUnsigned(a, b) < 0) 1 else 0 },
        eval64 = { a, b -> if (compareUnsignedLong(a, b) < 0) 1 else 0 },
        eval128 = { a, b -> if (compareUnsignedQuadWord(a, b) < 0) 1.toQuadWord() else 0.toQuadWord() }
)
