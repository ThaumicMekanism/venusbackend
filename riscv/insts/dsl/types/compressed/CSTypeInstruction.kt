package venusbackend.riscv.insts.dsl.types.compressed

import venusbackend.numbers.QuadWord
import venusbackend.riscv.insts.dsl.disasms.extensions.CSTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.extensions.CSTypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.b32.CSTypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.CSTypeParser
import venusbackend.riscv.insts.dsl.types.Instruction

class CSTypeInstruction(
    name: String,
    opcode2: Int,
    funct: Int,
    funct6: Int,
    eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Int, Int) -> Int,
    eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
    eval128: (QuadWord, QuadWord) -> QuadWord = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = CSTypeFormat(opcode2, funct, funct6),
        parser = CSTypeParser,
        impl16 = NoImplementation,
        impl32 = CSTypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = CSTypeDisassembler
)
