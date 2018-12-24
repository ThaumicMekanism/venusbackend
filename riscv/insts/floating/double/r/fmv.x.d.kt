package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.floating.FtRTypeInstruction

val fmvxd = FtRTypeInstruction(
        name = "fmv.x.d",
        opcode = 0b1010011,
        funct7 = 0b1110001,
        funct3 = 0b000,
        rs2 = 0b00000,
        eval32 = { a, b -> throw InstructionNotSupportedError("fmv.x.d is only for 64 bit systems!") }
)