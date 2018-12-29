package venusbackend.riscv.insts.dsl.types

import venusbackend.numbers.QuadWord
import venusbackend.riscv.insts.dsl.disasms.base.ShiftImmediateDisassembler
import venusbackend.riscv.insts.dsl.formats.base.RTypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.base.b32.ShiftImmediateImplementation32
import venusbackend.riscv.insts.dsl.impls.base.b64.ShiftImmediateImplementation64
import venusbackend.riscv.insts.dsl.parsers.base.ShiftImmediateParser

class ShiftImmediateInstruction(
    name: String,
    funct3: Int,
    funct7: Int,
    eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Int, Int) -> Int,
    eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
    eval128: (QuadWord, QuadWord) -> QuadWord = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = RTypeFormat(
                opcode = 0b0010011,
                funct3 = funct3,
                funct7 = funct7
        ),
        parser = ShiftImmediateParser,
        impl16 = NoImplementation,
        impl32 = ShiftImmediateImplementation32(eval32),
        impl64 = ShiftImmediateImplementation64(eval64),
        impl128 = NoImplementation,
        disasm = ShiftImmediateDisassembler
)
