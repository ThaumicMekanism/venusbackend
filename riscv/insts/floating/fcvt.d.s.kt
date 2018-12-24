package venusbackend.riscv.insts.floating

import venusbackend.riscv.insts.dsl.floating.FRRTypeInstruction

val fcvtds = FRRTypeInstruction(
        name = "fcvt.d.s",
        opcode = 0b1010011,
        funct7 = 0b0100001,
        rs2 = 0b00000,
        eval32 = { a, b -> Decimal(d = a.getCurrentFloat().toDouble(), isF = false) }
)