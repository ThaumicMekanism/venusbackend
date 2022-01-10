package venusbackend.riscv.insts.integer.base.i.trap

import venusbackend.riscv.insts.dsl.types.base.TRTypeInstruction

val mret = TRTypeInstruction(
        name = "mret",
        funct12 = 0b001100000010,
        opcode = 0b1110011
)