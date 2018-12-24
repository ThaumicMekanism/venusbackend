package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.FR4TypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val fmaddd = FR4TypeInstruction(
        name = "fmadd.d",
        opcode = 0b1000011,
        funct2 = 0b01,
        eval32 = { a, b, c -> Decimal(d = (a.getCurrentDouble() * b.getCurrentDouble()) + c.getCurrentDouble(), isF = false) }
)