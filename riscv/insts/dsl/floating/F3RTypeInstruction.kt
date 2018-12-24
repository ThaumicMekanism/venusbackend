package venusbackend.riscv.insts.dsl.floating

import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.disasms.extensions.FRTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.RTypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.FRTypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.FRTypeParser
import venusbackend.riscv.insts.floating.Decimal

/**
 * Created by thaum on 8/6/2018.
 */
class F3RTypeInstruction(
    name: String,
    opcode: Int,
    funct7: Int,
    funct3: Int,
        // eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Decimal, Decimal) -> Decimal // ,
        // eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
        // eval128: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = RTypeFormat(opcode, funct3, funct7),
        parser = FRTypeParser,
        impl16 = NoImplementation,
        impl32 = FRTypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = FRTypeDisassembler
)