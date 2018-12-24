package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.F3RTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val fmind = F3RTypeInstruction(
        name = "fmin.d",
        opcode = 0b1010011,
        funct3 = 0b000,
        funct7 = 0b0010101,
        eval32 = { a, b -> Decimal(d = minOf(a.getCurrentDouble(), b.getCurrentDouble()), isF = false) }
)