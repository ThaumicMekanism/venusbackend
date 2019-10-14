package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.extensions.floating.F3RTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

/*Single-Precision*/
val fmins = F3RTypeInstruction(
        name = "fmin.s",
        opcode = 0b1010011,
        funct3 = 0b000,
        funct7 = 0b0010100,
        eval32 = { a, b -> Decimal(f = minOf(a.getCurrentFloat(), b.getCurrentFloat())) }
)