package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.types.extensions.floating.FtRTypeInstruction
import kotlin.math.abs
import kotlin.math.round

val fcvtwud = FtRTypeInstruction(
        name = "fcvt.wu.d",
        opcode = 0b1010011,
        funct7 = 0b1100001,
        funct3 = 0b000,
        rs2 = 0b00001,
        eval32 = { a, b -> abs(round(a.getCurrentDouble())).toInt() }
)