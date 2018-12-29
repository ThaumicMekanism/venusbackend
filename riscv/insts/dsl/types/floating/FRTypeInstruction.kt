package venusbackend.riscv.insts.dsl.types.floating

import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.extensions.FRTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.extensions.FRTypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.b32.FRTypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.FRTypeParser
import venusbackend.riscv.insts.floating.Decimal

/**
 * Created by thaum on 8/6/2018.
 */
class FRTypeInstruction(
    name: String,
    opcode: Int,
    funct7: Int,
        // eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Decimal, Decimal) -> Decimal // ,
        // eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
        // eval128: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = FRTypeFormat(opcode, funct7),
        parser = FRTypeParser,
        impl16 = NoImplementation,
        impl32 = FRTypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = FRTypeDisassembler
)