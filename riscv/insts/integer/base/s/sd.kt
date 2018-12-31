package venusbackend.riscv.insts.integer.base.s

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.STypeInstruction

val sd = STypeInstruction(
        name = "sd",
        opcode = 0b0100011,
        funct3 = 0b011,
        store16 = { sim, address, value ->
            throw InstructionNotSupportedError("sd is not supported on 32 bit systems!")
        },
        store32 = { sim, address, value ->
            throw InstructionNotSupportedError("sd is not supported on 64 bit systems!")
        },
        store64 = { sim, address, value ->
            sim.storeLongwCache(address, value)
        },
        store128 = { sim, address, value ->
            sim.storeLongwCache(address, value)
        }
)
