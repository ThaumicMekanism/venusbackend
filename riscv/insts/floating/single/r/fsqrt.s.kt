package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.extensions.floating.FRRTypeInstruction
import venusbackend.riscv.insts.floating.Decimal
import kotlin.math.sqrt

/*Single-Precision*/
val fsqrts = FRRTypeInstruction(
        name = "fsqrt.s",
        opcode = 0b1010011,
        funct7 = 0b0101100,
        rs2 = 0b00000,
        eval32 = { a, b -> Decimal(f = sqrt((a.getCurrentFloat()).toDouble()).toFloat()) }
)