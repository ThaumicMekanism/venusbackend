package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.dsl.LoadTypeInstruction
import venusbackend.simulator.Simulator

val lw = LoadTypeInstruction(
        name = "lw",
        opcode = 0b0000011,
        funct3 = 0b010,
//        load16 = Simulator::loadWordwCache
        load32 = Simulator::loadWordwCache,
        load64 = { sim, addr -> sim.loadWordwCache(addr).toLong() }
//        load128 = Simulator::loadWordwCache
)
