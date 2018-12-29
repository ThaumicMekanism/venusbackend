package venusbackend.riscv.insts.floating

import venusbackend.riscv.insts.dsl.types.floating.FRRTypeInstruction

val fcvtsd = FRRTypeInstruction(
        name = "fcvt.s.d",
        opcode = 0b1010011,
        funct7 = 0b0100000,
        rs2 = 0b00001,
        eval32 = { a, b -> Decimal(f = a.getCurrentDouble().toFloat()) }
)