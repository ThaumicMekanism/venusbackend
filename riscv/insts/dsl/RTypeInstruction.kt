package venusbackend.riscv.insts.dsl

import venusbackend.riscv.insts.dsl.disasms.base.RTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.RTypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.base.RTypeImplementation32
import venusbackend.riscv.insts.dsl.impls.base.RTypeImplementation64
import venusbackend.riscv.insts.dsl.parsers.base.RTypeParser

class RTypeInstruction(
    name: String,
    opcode: Int,
    funct3: Int,
    funct7: Int = 0b0,
    eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Int, Int) -> Int,
    eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
    eval128: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = RTypeFormat(opcode, funct3, funct7),
        parser = RTypeParser,
        impl16 = NoImplementation,
        impl32 = RTypeImplementation32(eval32),
        impl64 = RTypeImplementation64(eval64),
        impl128 = NoImplementation,
        disasm = RTypeDisassembler
)
