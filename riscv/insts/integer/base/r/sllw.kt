package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.RTypeInstruction

val sllw = RTypeInstruction(
        name = "sllw",
        opcode = 0b0111011,
        funct3 = 0b001,
        funct7 = 0b0000000,
        eval32 = { a, b ->
            throw InstructionNotSupportedError("addiw is not supported on 32 bit systems!")
        }
)