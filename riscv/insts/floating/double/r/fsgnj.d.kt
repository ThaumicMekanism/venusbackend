package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.F3RTypeInstruction
import venusbackend.riscv.insts.floating.Decimal
import kotlin.math.withSign

val fsgnjd = F3RTypeInstruction(
        name = "fsgnj.d",
        opcode = 0b1010011,
        funct7 = 0b0010001,
        funct3 = 0b000,
        eval32 = { a, b -> Decimal(d = a.getCurrentDouble().withSign(b.getCurrentDouble()), isF = false) }
)