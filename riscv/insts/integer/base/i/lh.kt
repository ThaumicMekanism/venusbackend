package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.LoadTypeInstruction
import venusbackend.riscv.insts.dsl.impls.signExtend

val lh = LoadTypeInstruction(
        name = "lh",
        opcode = 0b0000011,
        funct3 = 0b001,
        load16 = { sim, address -> sim.loadHalfWordwCache(address).toShort() },
        postLoad16 = { v -> v },
        load32 = { sim, address -> sim.loadHalfWordwCache(address) },
        postLoad32 = { v -> signExtend(v, 16) },
        load64 = { sim, address -> sim.loadHalfWordwCache(address).toLong() },
        postLoad64 = { v -> signExtend(v.toInt(), 16).toLong() },
        load128 = { sim, address -> sim.loadHalfWordwCache(address).toQuadWord() },
        postLoad128 = { v -> signExtend(v.toInt(), 16).toQuadWord() }
)
