package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.extensions.floating.FRTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

/*Single-Precision*/
val fmuls = FRTypeInstruction(
        name = "fmul.s",
        opcode = 0b1010011,
        funct7 = 0b0001000,
        eval32 = { a, b -> Decimal(f = a.getCurrentFloat() * b.getCurrentFloat()) }
)