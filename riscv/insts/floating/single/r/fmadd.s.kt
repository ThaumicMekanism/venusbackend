package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.floating.FR4TypeInstruction
import venusbackend.riscv.insts.floating.Decimal

/*Single-Precision*/
val fmadds = FR4TypeInstruction(
        name = "fmadd.s",
        opcode = 0b1000011,
        funct2 = 0b00,
        eval32 = { a, b, c -> Decimal(f = (a.getCurrentFloat() * b.getCurrentFloat()) + c.getCurrentFloat()) }
)