package venusbackend.riscv.insts.dsl.floating

import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.disasms.extensions.FSTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.STypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.b32.FSTypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.FSTypeParser
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.Simulator

class FSTypeInstruction(
    name: String,
    opcode: Int,
    funct3: Int,
    store32: (Simulator, Int, Decimal) -> Unit
) : Instruction(
        name = name,
        format = STypeFormat(opcode, funct3),
        parser = FSTypeParser,
        impl16 = NoImplementation,
        impl32 = FSTypeImplementation32(store32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = FSTypeDisassembler
)