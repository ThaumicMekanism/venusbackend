package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.extensions.floating.FR4TypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val fnmadds = FR4TypeInstruction(
        name = "fnmadd.s",
        opcode = 0b1001111,
        funct2 = 0b00,
        eval32 = { a, b, c -> Decimal(f = -((a.getCurrentFloat() * b.getCurrentFloat()) + c.getCurrentFloat())) }
)