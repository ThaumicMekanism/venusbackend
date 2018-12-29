package venusbackend.riscv.insts.integer.base.s

import venusbackend.riscv.insts.dsl.types.STypeInstruction
import venusbackend.simulator.Simulator

val sb = STypeInstruction(
        name = "sb",
        opcode = 0b0100011,
        funct3 = 0b000,
//        store16 = NoImplementation,
        store32 = Simulator::storeBytewCache,
        store64 = Simulator::storeBytewCache
//        store128 = NoImplementation
)
