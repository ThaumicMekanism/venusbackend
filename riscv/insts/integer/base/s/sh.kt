package venusbackend.riscv.insts.integer.base.s

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.STypeInstruction
import venusbackend.simulator.Simulator

val sh = STypeInstruction(
        name = "sh",
        opcode = 0b0100011,
        funct3 = 0b001,
        store16 = { sim, address, value ->
            sim.storeHalfWordwCache(address, value)
        },
        store32 = { sim, address, value ->
            sim.storeHalfWordwCache(address, value)
        },
        store64 = { sim, address, value ->
            sim.storeHalfWordwCache(address, value)
        },
        store128 = { sim, address, value ->
            sim.storeHalfWordwCache(address, value)
        }
)
