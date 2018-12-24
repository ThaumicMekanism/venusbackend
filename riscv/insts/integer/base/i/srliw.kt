package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.ShiftWImmediateInstruction

val srliw = ShiftWImmediateInstruction(
        name = "srliw",
        opcode = 0b0011011,
        funct3 = 0b101,
        funct7 = 0b0000000,
        eval32 = { a, b ->
            throw InstructionNotSupportedError("addiw is not supported on 32 bit systems!")
        }
)