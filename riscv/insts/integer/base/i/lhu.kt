package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.dsl.LoadTypeInstruction

val lhu = LoadTypeInstruction(
        name = "lhu",
        opcode = 0b0000011,
        funct3 = 0b101,
//        load16 = NoImplementation,
        load32 = { sim, address -> sim.loadHalfWordwCache(address) }
//        load64 = NoImplementation,
//        load128 = NoImplementation
)
