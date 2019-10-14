package venusbackend.riscv.insts.dsl.types.extensions.floating

import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.extensions.floating.FRRTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.extensions.floating.FSRS2TypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.floating.b32.FRTypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.floating.FRRTypeParser
import venusbackend.riscv.insts.floating.Decimal

class FRRTypeInstruction(
    name: String,
    opcode: Int,
    funct7: Int,
    rs2: Int,
        // eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Decimal, Decimal) -> Decimal // ,
        // eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
        // eval128: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = FSRS2TypeFormat(opcode, funct7, rs2),
        parser = FRRTypeParser,
        impl16 = NoImplementation,
        impl32 = FRTypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = FRRTypeDisassembler
)