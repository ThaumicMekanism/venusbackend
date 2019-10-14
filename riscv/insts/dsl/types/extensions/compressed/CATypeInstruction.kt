package venusbackend.riscv.insts.dsl.types.extensions.compressed

import venusbackend.numbers.QuadWord
import venusbackend.riscv.insts.dsl.disasms.extensions.compressed.CATypeDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.extensions.compressed.CATypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.compressed.b32.CATypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.compressed.CATypeParser
import venusbackend.riscv.insts.dsl.types.Instruction

class CATypeInstruction(
    name: String,
    opcode2: Int,
    funct2: Int,
    funct6: Int,
    regComp: Array<FieldEqual> = arrayOf(),
    eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Int, Int) -> Int,
    eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
    eval128: (QuadWord, QuadWord) -> QuadWord = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = CATypeFormat(opcode2, funct2, funct6),
        parser = CATypeParser,
        impl16 = NoImplementation,
        impl32 = CATypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = CATypeDisassembler
)
