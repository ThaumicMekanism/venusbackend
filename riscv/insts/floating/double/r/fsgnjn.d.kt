package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.F3RTypeInstruction
import venusbackend.riscv.insts.floating.Decimal
import kotlin.math.sign
import kotlin.math.withSign

val fsgnjnd = F3RTypeInstruction(
        name = "fsgnjn.d",
        opcode = 0b1010011,
        funct7 = 0b0010001,
        funct3 = 0b001,
        /*TODO FIX THE BITS TO MAKE THE SIGN CORRECT*/
        eval32 = { a, b -> Decimal(d = a.getCurrentDouble().withSign((b.getCurrentDouble().toRawBits() xor 0x800000000000000).sign), isF = false) }
)