package venusbackend.riscv.insts.floating.double.s

import venusbackend.riscv.insts.dsl.types.extensions.floating.FSTypeInstruction

val fsd = FSTypeInstruction(
        name = "fsd",
        opcode = 0b0100111,
        funct3 = 0b011,
//        store16 = NoImplementation,
        store32 = { sim, address, value ->
            sim.storeWordwCache(address, (value.getCurrentDouble().toRawBits() and 0xFFFFFFFF).toInt())
            sim.storeWordwCache(address + 4, ((value.getCurrentDouble().toRawBits() shr 32) and 0xFFFFFFFF).toInt())
        }
//        store64 = NoImplementation,
//        store128 = NoImplementation
)