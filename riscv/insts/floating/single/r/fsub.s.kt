package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.floating.FRTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

/*Single-Precision*/
val fsubs = FRTypeInstruction(
        name = "fsub.s",
        opcode = 0b1010011,
        funct7 = 0b0000100,
        eval32 = { a, b -> Decimal(f = a.getCurrentFloat() - b.getCurrentFloat()) }
)