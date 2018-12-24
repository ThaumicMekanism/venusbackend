package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.dsl.LoadTypeInstruction
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.simulator.Simulator

val lb = LoadTypeInstruction(
        name = "lb",
        opcode = 0b0000011,
        funct3 = 0b000,
//        load16 = NoImplementation,
//        postLoad16 = NoImplementation,
        load32 = Simulator::loadBytewCache,
        postLoad32 = { v -> signExtend(v, 8) }
//        load64 = NoImplementation,
//        postLoad64 = NoImplementation,
//        load128 = NoImplementation,
//        postLoad128 = NoImplementation
)
