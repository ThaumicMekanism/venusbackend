package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.types.extensions.floating.FFRRTypeInstruction

val fltd = FFRRTypeInstruction(
        name = "flt.d",
        opcode = 0b1010011,
        funct3 = 0b001,
        funct7 = 0b1010001,
        eval32 = { a, b -> if (a.getCurrentDouble() < b.getCurrentDouble()) 1 else 0 }
)