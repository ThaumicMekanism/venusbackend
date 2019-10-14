package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.types.extensions.floating.FRTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val fsubd = FRTypeInstruction(
        name = "fsub.d",
        opcode = 0b1010011,
        funct7 = 0b0000101,
        eval32 = { a, b -> Decimal(d = a.getCurrentDouble() - b.getCurrentDouble(), isF = false) }
)