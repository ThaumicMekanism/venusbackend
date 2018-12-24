package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.F3RTypeInstruction
import venusbackend.riscv.insts.floating.Decimal
import kotlin.math.sign
import kotlin.math.withSign

val fsgnjxd = F3RTypeInstruction(
        name = "fsgnjx.d",
        opcode = 0b1010011,
        funct7 = 0b0010001,
        funct3 = 0b010,
        eval32 = { a, b -> Decimal(d = a.getCurrentDouble().withSign((a.getCurrentDouble().toRawBits() xor b.getCurrentDouble().toRawBits()).sign), isF = false) }
)