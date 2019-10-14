package venusbackend.riscv.insts.dsl.types.base

import venusbackend.numbers.QuadWord
import venusbackend.riscv.insts.dsl.disasms.base.ITypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.ITypeFormat
import venusbackend.riscv.insts.dsl.impls.base.b32.ITypeImplementation32
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.base.b64.ITypeImplementation64
import venusbackend.riscv.insts.dsl.parsers.base.ITypeParser
import venusbackend.riscv.insts.dsl.types.Instruction

class ITypeInstruction(
    name: String,
    opcode: Int,
    funct3: Int,
    eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Int, Int) -> Int,
    eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
    eval128: (QuadWord, QuadWord) -> QuadWord = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = ITypeFormat(opcode, funct3),
        parser = ITypeParser,
        impl16 = NoImplementation,
        impl32 = ITypeImplementation32(eval32),
        impl64 = ITypeImplementation64(eval64),
        impl128 = NoImplementation,
        disasm = ITypeDisassembler
)
