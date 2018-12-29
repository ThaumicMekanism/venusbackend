package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.floating.RtFTypeInstruction

val fmvdx = RtFTypeInstruction(
        name = "fmv.d.x",
        opcode = 0b1010011,
        funct7 = 0b1111001,
        funct3 = 0b000,
        rs2 = 0b00000,
        eval32 = { a -> throw InstructionNotSupportedError("fmv.d.x is only for 64 bit systems!") }
)