package venusbackend.riscv.insts.integer.base.sb

import venusbackend.riscv.insts.dsl.types.BTypeInstruction
import venusbackend.riscv.insts.dsl.compareUnsigned
import venusbackend.riscv.insts.dsl.compareUnsignedLong
import venusbackend.riscv.insts.dsl.compareUnsignedQuadWord
import venusbackend.riscv.insts.dsl.compareUnsignedShort

val bgeu = BTypeInstruction(
        name = "bgeu",
        opcode = 0b1100011,
        funct3 = 0b111,
        cond16 = { a, b -> compareUnsignedShort(a, b) >= 0 },
        cond32 = { a, b -> compareUnsigned(a, b) >= 0 },
        cond64 = { a, b -> compareUnsignedLong(a, b) >= 0 },
        cond128 = { a, b -> compareUnsignedQuadWord(a, b) >= 0 }
)
