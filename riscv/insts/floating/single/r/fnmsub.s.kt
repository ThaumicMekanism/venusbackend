package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.floating.FR4TypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val fnmsubs = FR4TypeInstruction(
        name = "fnmsub.s",
        opcode = 0b1001011,
        funct2 = 0b00,
        eval32 = { a, b, c -> Decimal(f = -((a.getCurrentFloat() * b.getCurrentFloat()) - c.getCurrentFloat())) }
)