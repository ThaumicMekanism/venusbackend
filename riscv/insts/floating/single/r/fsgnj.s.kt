package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.floating.F3RTypeInstruction
import venusbackend.riscv.insts.floating.Decimal
import kotlin.math.withSign

/*Single-Precision*/
val fsgnjs = F3RTypeInstruction(
        name = "fsgnj.s",
        opcode = 0b1010011,
        funct7 = 0b0010000,
        funct3 = 0b000,
        eval32 = { a, b -> Decimal(f = a.getCurrentFloat().withSign(b.getCurrentFloat())) }
)