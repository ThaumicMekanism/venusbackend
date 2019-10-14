package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.base.LoadTypeInstruction

val lhu = LoadTypeInstruction(
        name = "lhu",
        opcode = 0b0000011,
        funct3 = 0b101,
        load16 = { sim, address -> sim.loadHalfWordwCache(address).toShort() },
        load32 = { sim, address -> sim.loadHalfWordwCache(address) },
        load64 = { sim, address -> sim.loadHalfWordwCache(address).toLong() },
        load128 = { sim, address -> sim.loadHalfWordwCache(address).toQuadWord() }
)
