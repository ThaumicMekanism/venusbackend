package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.LoadTypeInstruction
import venusbackend.simulator.Simulator

val ld = LoadTypeInstruction(
        name = "ld",
        opcode = 0b0000011,
        funct3 = 0b011,
//        load16 = Simulator::loadWordwCache
        load32 = { a, b ->
            throw InstructionNotSupportedError("LD is not supported by 32 bit systems!")
        },
        load64 = Simulator::loadLongwCache
//        load128 = Simulator::loadWordwCache
)