package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.insts.dsl.LoadTypeInstruction
import venusbackend.simulator.Simulator

val lbu = LoadTypeInstruction(
        name = "lbu",
        opcode = 0b0000011,
        funct3 = 0b100,
//        load16 = NoImplementation,
        load32 = Simulator::loadBytewCache
//        load64 = NoImplementation,
//        load128 = NoImplementation,
)
