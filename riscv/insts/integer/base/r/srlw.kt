package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.RTypeInstruction

val srlw = RTypeInstruction(
        name = "srlw",
        opcode = 0b0111011,
        funct3 = 0b101,
        funct7 = 0b0000000,
        eval32 = { a, b ->
            throw InstructionNotSupportedError("addiw is not supported on 32 bit systems!")
        }
)