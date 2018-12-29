package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.floating.FRTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

/*Single-Precision*/
val fadds = FRTypeInstruction(
        name = "fadd.s",
        opcode = 0b1010011,
        funct7 = 0b0000000,
        eval32 = { a, b -> Decimal(f = a.getCurrentFloat() + b.getCurrentFloat()) }
)