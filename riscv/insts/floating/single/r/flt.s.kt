package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.extensions.floating.FFRRTypeInstruction

/*Single-Precision*/
val flts = FFRRTypeInstruction(
        name = "flt.s",
        opcode = 0b1010011,
        funct3 = 0b001,
        funct7 = 0b1010000,
        eval32 = { a, b -> if (a.getCurrentFloat() < b.getCurrentFloat()) 1 else 0 }
)