package venusbackend.riscv.insts.floating.single.s

import venusbackend.riscv.insts.dsl.floating.FSTypeInstruction

/*Single-Precision*/

val fsw = FSTypeInstruction(
        name = "fsw",
        opcode = 0b0100111,
        funct3 = 0b010,
//        store16 = NoImplementation,
        store32 = { sim, address, value ->
            sim.storeWordwCache(address, value.getCurrentFloat().toRawBits())
        }
//        store64 = NoImplementation,
//        store128 = NoImplementation
)