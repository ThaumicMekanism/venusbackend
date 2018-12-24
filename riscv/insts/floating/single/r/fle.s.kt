package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.floating.FFRRTypeInstruction

/*Single-Precision*/
val fles = FFRRTypeInstruction(
        name = "fle.s",
        opcode = 0b1010011,
        funct3 = 0b000,
        funct7 = 0b1010000,
        eval32 = { a, b -> if (a.getCurrentFloat() <= b.getCurrentFloat()) 1 else 0 }
)