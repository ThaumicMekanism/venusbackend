package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.dsl.LoadTypeInstruction
import venusbackend.riscv.insts.dsl.impls.signExtend

val lh = LoadTypeInstruction(
        name = "lh",
        opcode = 0b0000011,
        funct3 = 0b001,
//        load16 = Simulator::loadHalfWordwCache,
//        postLoad16 = { v -> signExtend(v, 16) },
        load32 = { sim, address -> sim.loadHalfWordwCache(address).toInt() },
        postLoad32 = { v -> signExtend(v, 16) }
//        load64 = Simulator::loadHalfWordwCache,
//        postLoad64 = { v -> signExtend(v, 16) },
//        load128 = Simulator::loadHalfWordwCache,
//        postLoad128 = { v -> signExtend(v, 16) }
)
