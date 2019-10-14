package venusbackend.riscv.insts.integer.extensions.compressed.ca

import venusbackend.riscv.insts.InstructionReservedError
import venusbackend.riscv.insts.dsl.types.extensions.compressed.CATypeInstruction

val csubw = CATypeInstruction(
        name = "c.subw",
        opcode2 = 0b01,
        funct2 = 0b00,
        funct6 = 0b100111,
        eval32 = { _, _ ->
            throw InstructionReservedError("This instruction is reserved in RV32")
        }
)