package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.insts.dsl.types.LoadTypeInstruction

val lbu = LoadTypeInstruction(
        name = "lbu",
        opcode = 0b0000011,
        funct3 = 0b100,
//        load16 = NoImplementation,
        load32 = { sim, addr -> sim.loadBytewCache(addr).toInt() },
        load64 = { sim, addr -> sim.loadBytewCache(addr).toLong() },
        load128 = { sim, addr -> sim.loadBytewCache(addr).toQuadWord() }
)
