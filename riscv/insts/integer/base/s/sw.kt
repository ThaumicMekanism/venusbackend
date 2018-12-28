package venusbackend.riscv.insts.integer.base.s

import venusbackend.riscv.insts.dsl.STypeInstruction
import venusbackend.simulator.Simulator

val sw = STypeInstruction(
        name = "sw",
        opcode = 0b0100011,
        funct3 = 0b010,
//        store16 = Simulator::storeWordwCache,
        store32 = Simulator::storeWordwCache,
        store64 = Simulator::storeWordwCache
//        store128 = Simulator::storeWordwCache
)
