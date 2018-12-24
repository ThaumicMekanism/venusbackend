package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.ITypeInstruction

val addiw = ITypeInstruction(
        name = "addiw",
        opcode = 0b0011011,
        funct3 = 0b000,
        eval32 = { a, b ->
            throw InstructionNotSupportedError("addiw is not supported on 32 bit systems!")
        }
)