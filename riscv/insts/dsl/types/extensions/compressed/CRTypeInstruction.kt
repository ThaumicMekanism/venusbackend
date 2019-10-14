package venusbackend.riscv.insts.dsl.types.extensions.compressed

import venusbackend.numbers.QuadWord
import venusbackend.riscv.insts.dsl.disasms.extensions.compressed.CRTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.extensions.compressed.CRTypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.compressed.b32.CRTypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.compressed.CRTypeParser
import venusbackend.riscv.insts.dsl.types.Instruction

class CRTypeInstruction(
    name: String,
    opcode2: Int,
    funct4: Int,
    regComp: Array<FieldEqual> = arrayOf(),
    eval16: (Short, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    eval32: (Int, Int) -> Int,
    eval64: (Long, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
    eval128: (QuadWord, QuadWord) -> QuadWord = { _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = CRTypeFormat(opcode2, funct4, regComp),
        parser = CRTypeParser,
        impl16 = NoImplementation,
        impl32 = CRTypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = CRTypeDisassembler
)
