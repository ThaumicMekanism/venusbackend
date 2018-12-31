package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toUQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.LoadTypeInstruction
import venusbackend.simulator.Simulator

val lwu = LoadTypeInstruction(
    name = "lwu",
    opcode = 0b0000011,
    funct3 = 0b010,
    load16 = { sim, address ->
        throw InstructionNotSupportedError("lwu is not supported by 16 bit systems!")
    },
    load32 = Simulator::loadWordwCache,
    load64 = { sim, address ->
        sim.loadWordwCache(address).toULong().toLong()
    },
    load128 = { sim, address ->
        sim.loadWordwCache(address).toUQuadWord()
    }
)