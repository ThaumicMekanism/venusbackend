package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.FtRTypeInstruction
import kotlin.math.round

val fcvtwd = FtRTypeInstruction(
        name = "fcvt.w.d",
        opcode = 0b1010011,
        funct7 = 0b1100001,
        funct3 = 0b000,
        rs2 = 0b00000,
        eval32 = { a, b -> round(a.getCurrentDouble()).toInt() }
)