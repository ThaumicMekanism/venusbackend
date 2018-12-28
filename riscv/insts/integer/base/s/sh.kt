package venusbackend.riscv.insts.integer.base.s

import venusbackend.riscv.insts.dsl.STypeInstruction
import venusbackend.simulator.Simulator

val sh = STypeInstruction(
        name = "sh",
        opcode = 0b0100011,
        funct3 = 0b001,
//        store16 = NoImplementation,
        store32 = Simulator::storeHalfWordwCache,
        store64 = Simulator::storeHalfWordwCache
//        store128 = NoImplementation
)
