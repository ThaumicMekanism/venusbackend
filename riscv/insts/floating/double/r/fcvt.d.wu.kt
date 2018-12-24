package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.RtFTypeInstruction
import venusbackend.riscv.insts.floating.Decimal
import kotlin.math.abs

val fcvtdwu = RtFTypeInstruction(
        name = "fcvt.d.wu",
        opcode = 0b1010011,
        funct7 = 0b1101001,
        funct3 = 0b000,
        rs2 = 0b00001,
        eval32 = { a -> Decimal(d = abs(a).toDouble(), isF = false) }
)