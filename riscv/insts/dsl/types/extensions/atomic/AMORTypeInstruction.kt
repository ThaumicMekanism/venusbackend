package venusbackend.riscv.insts.dsl.types.extensions.atomic

import venusbackend.numbers.QuadWord
import venusbackend.riscv.insts.dsl.disasms.extensions.atomic.AMORTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.extensions.atomic.AMORTypeFormat
import venusbackend.riscv.insts.dsl.impls.extensions.atomic.b32.AMORTypeImplementation32
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.parsers.extensions.atomic.AMORTypeParser
import venusbackend.riscv.insts.dsl.types.Instruction

class AMORTypeInstruction(
    name: String,
    opcode: Int,
    funct3: Int,
    aq: Int,
    rl: Int,
    funct5: Int,
    eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Int, Int) -> Int,
    eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
    eval128: (QuadWord, QuadWord) -> QuadWord = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = AMORTypeFormat(opcode, funct3, funct5, aq, rl),
        parser = AMORTypeParser,
        impl16 = NoImplementation,
        impl32 = AMORTypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = AMORTypeDisassembler
)
