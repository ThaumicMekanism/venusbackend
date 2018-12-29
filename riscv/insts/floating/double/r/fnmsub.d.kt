package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.types.floating.FR4TypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val fnmsubd = FR4TypeInstruction(
        name = "fnmsub.d",
        opcode = 0b1001011,
        funct2 = 0b01,
        eval32 = { a, b, c -> Decimal(d = -((a.getCurrentDouble() * b.getCurrentDouble()) - c.getCurrentDouble()), isF = false) }
)