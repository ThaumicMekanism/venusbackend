package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.LoadTypeInstruction
import venusbackend.simulator.Simulator

val lw = LoadTypeInstruction(
        name = "lw",
        opcode = 0b0000011,
        funct3 = 0b010,
        load16 = { sim, addr ->
            throw InstructionNotSupportedError("LW is not supported by 16 bit systems!")
        },
        load32 = Simulator::loadWordwCache,
        load64 = { sim, addr -> sim.loadWordwCache(addr).toLong() },
        load128 = { sim, addr -> sim.loadWordwCache(addr).toQuadWord() }
)
