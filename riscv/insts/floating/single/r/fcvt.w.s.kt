package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.floating.FtRTypeInstruction
import kotlin.math.round

val fcvtws = FtRTypeInstruction(
        name = "fcvt.w.s",
        opcode = 0b1010011,
        funct7 = 0b1100000,
        funct3 = 0b000,
        rs2 = 0b00000,
        eval32 = { a, b -> round(a.getCurrentFloat()).toInt() }
)