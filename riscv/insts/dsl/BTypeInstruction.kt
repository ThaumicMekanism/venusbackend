package venusbackend.riscv.insts.dsl

import venusbackend.riscv.insts.dsl.disasms.base.BTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.BTypeFormat
import venusbackend.riscv.insts.dsl.impls.base.b32.BTypeImplementation32
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.base.b64.BTypeImplementation64
import venusbackend.riscv.insts.dsl.parsers.base.BTypeParser

class BTypeInstruction(
    name: String,
    opcode: Int,
    funct3: Int,
    cond16: (Short, Short) -> Boolean = { _, _ -> throw NotImplementedError("no rv64") },
    cond32: (Int, Int) -> Boolean,
    cond64: (Long, Long) -> Boolean = { _, _ -> throw NotImplementedError("no rv64") },
    cond128: (Long, Long) -> Boolean = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = BTypeFormat(opcode, funct3),
        parser = BTypeParser,
        impl16 = NoImplementation,
        impl32 = BTypeImplementation32(cond32),
        impl64 = BTypeImplementation64(cond64),
        impl128 = NoImplementation,
        disasm = BTypeDisassembler
)
