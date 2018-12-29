package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.types.floating.FRTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val fmuld = FRTypeInstruction(
        name = "fmul.d",
        opcode = 0b1010011,
        funct7 = 0b0001001,
        eval32 = { a, b -> Decimal(d = a.getCurrentDouble() * b.getCurrentDouble(), isF = false) }
)