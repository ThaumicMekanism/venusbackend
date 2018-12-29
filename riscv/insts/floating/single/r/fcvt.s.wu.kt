package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.floating.RtFTypeInstruction
import venusbackend.riscv.insts.floating.Decimal
import kotlin.math.abs

val fcvtswu = RtFTypeInstruction(
        name = "fcvt.s.wu",
        opcode = 0b1010011,
        funct7 = 0b1101000,
        funct3 = 0b000,
        rs2 = 0b00001,
        eval32 = { a -> Decimal(f = abs(a).toFloat()) }
)