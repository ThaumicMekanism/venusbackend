package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.types.LoadTypeInstruction
import venusbackend.simulator.Simulator

val ld = LoadTypeInstruction(
        name = "ld",
        opcode = 0b0000011,
        funct3 = 0b011,
        load16 = { a, b ->
            throw InstructionNotSupportedError("LD is not supported by 16 bit systems!")
        },
        load32 = { a, b ->
            throw InstructionNotSupportedError("LD is not supported by 32 bit systems!")
        },
        load64 = Simulator::loadLongwCache,
        load128 = { sim, addr ->
            sim.loadLongwCache(addr).toQuadWord()
        }
)