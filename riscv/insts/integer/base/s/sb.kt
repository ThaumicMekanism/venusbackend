package venusbackend.riscv.insts.integer.base.s

import venusbackend.riscv.insts.dsl.types.STypeInstruction
import venusbackend.simulator.Simulator

val sb = STypeInstruction(
        name = "sb",
        opcode = 0b0100011,
        funct3 = 0b000,
        store16 = { sim, address, value ->
            sim.storeBytewCache(address, value)
        },
        store32 = { sim, address, value ->
            sim.storeBytewCache(address, value)
        },
        store64 = { sim, address, value ->
            sim.storeBytewCache(address, value)
        },
        store128 = { sim, address, value ->
            sim.storeBytewCache(address, value)
        }
)
