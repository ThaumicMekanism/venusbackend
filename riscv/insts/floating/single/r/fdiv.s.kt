package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.floating.FRTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

/*Single-Precision*/
val fdivs = FRTypeInstruction(
        name = "fdiv.s",
        opcode = 0b1010011,
        funct7 = 0b0001100,
        eval32 = { a, b -> Decimal(f = a.getCurrentFloat() / b.getCurrentFloat()) }
)