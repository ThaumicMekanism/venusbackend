package venusbackend.riscv.insts.integer.base.r

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.RTypeInstruction

val addw = RTypeInstruction(
        name = "addw",
        opcode = 0b0111011,
        funct3 = 0b000,
        funct7 = 0b0000000,
        eval32 = { a, b ->
            throw InstructionNotSupportedError("addiw is not supported on 32 bit systems!")
        }
)