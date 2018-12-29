package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.types.floating.RtFTypeInstruction
import venusbackend.riscv.insts.floating.Decimal

val fcvtdw = RtFTypeInstruction(
        name = "fcvt.d.w",
        opcode = 0b1010011,
        funct7 = 0b1101001,
        funct3 = 0b000,
        rs2 = 0b00000,
        eval32 = { a -> Decimal(d = a.toDouble(), isF = false) }
)