package venusbackend.riscv.insts.integer.base.s

import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.STypeInstruction

val sd = STypeInstruction(
        name = "sd",
        opcode = 0b0100011,
        funct3 = 0b011,
//        store16 = Simulator::storeWordwCache,
        store32 = { sim, a, b ->
            throw InstructionNotSupportedError("SD is not supported by 32 bit systems!")
        },
        store64 = { sim, a, b ->
            sim.storeWordwCache(a, b)
        }
//        store128 = Simulator::storeWordwCache
)
