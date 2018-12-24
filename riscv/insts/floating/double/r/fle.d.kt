package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.FFRRTypeInstruction

val fled = FFRRTypeInstruction(
        name = "fle.d",
        opcode = 0b1010011,
        funct3 = 0b000,
        funct7 = 0b1010001,
        eval32 = { a, b -> if (a.getCurrentDouble() <= b.getCurrentDouble()) 1 else 0 }
)