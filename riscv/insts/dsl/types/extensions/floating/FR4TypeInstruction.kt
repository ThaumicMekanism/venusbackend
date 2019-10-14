package venusbackend.riscv.insts.dsl.types.extensions.floating

import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.extensions.floating.FR4TypeDisassembler
import venusbackend.riscv.insts.dsl.formats.extensions.floating.R4TypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.floating.b32.FR4TypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.floating.FR4TypeParser
import venusbackend.riscv.insts.floating.Decimal

class FR4TypeInstruction(
    name: String,
    opcode: Int,
    funct2: Int,
        // eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Decimal, Decimal, Decimal) -> Decimal // ,
        // eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
        // eval128: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = R4TypeFormat(opcode, funct2),
        parser = FR4TypeParser,
        impl16 = NoImplementation,
        impl32 = FR4TypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = FR4TypeDisassembler
)