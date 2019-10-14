package venusbackend.riscv.insts.integer.base.s

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.base.STypeInstruction

val sw = STypeInstruction(
        name = "sw",
        opcode = 0b0100011,
        funct3 = 0b010,
        store16 = { sim, address, value ->
            throw InstructionNotSupportedError("sw is not supported on 16 bit systems!")
        },
        store32 = { sim, address, value ->
            sim.storeWordwCache(address, value)
        },
        store64 = { sim, address, value ->
            sim.storeWordwCache(address, value)
        },
        store128 = { sim, address, value ->
            sim.storeWordwCache(address, value)
        }
)
