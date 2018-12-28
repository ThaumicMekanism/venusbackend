package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.dsl.LoadTypeInstruction
import venusbackend.riscv.insts.dsl.impls.signExtend

val lb = LoadTypeInstruction(
        name = "lb",
        opcode = 0b0000011,
        funct3 = 0b000,
//        load16 = NoImplementation,
//        postLoad16 = NoImplementation,
        load32 = { sim, address -> sim.loadBytewCache(address).toInt() },
        postLoad32 = { v -> signExtend(v, 8) },
        load64 = { sim, address -> sim.loadBytewCache(address).toLong() },
        postLoad64 = { v -> signExtend(v.toInt(), 8).toLong() }
//        load128 = NoImplementation,
//        postLoad128 = NoImplementation
)
