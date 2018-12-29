package venusbackend.riscv.insts.floating.single.r

import venusbackend.riscv.insts.dsl.types.floating.FtRTypeInstruction

/*Single-Precision*/
val fmvxs = FtRTypeInstruction(
        name = "fmv.x.s",
        opcode = 0b1010011,
        funct7 = 0b1110000,
        funct3 = 0b000,
        rs2 = 0b00000,
        eval32 = { a, b -> a.getCurrentFloat().toRawBits() }
)