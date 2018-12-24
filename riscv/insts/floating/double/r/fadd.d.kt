package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.FRTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val faddd = FRTypeInstruction(
        name = "fadd.d",
        opcode = 0b1010011,
        funct7 = 0b0000001,
        eval32 = { a, b -> Decimal(d = a.getCurrentDouble() + b.getCurrentDouble(), isF = false) }
)