package venusbackend.riscv.insts.dsl.types.base

import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.base.TRTypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.TRTypeImplementation
import venusbackend.riscv.insts.dsl.parsers.DoNothingParser
import venusbackend.riscv.insts.dsl.types.Instruction

class TRTypeInstruction(
    name: String,
    opcode: Int,
    funct12: Int
) : Instruction(
        name = name,
        parser = DoNothingParser,
        disasm = RawDisassembler{name},
        format = TRTypeFormat(opcode, funct12),
        impl16 = NoImplementation,
        impl32 = TRTypeImplementation(),
        impl64 = TRTypeImplementation(),
        impl128 = NoImplementation
)