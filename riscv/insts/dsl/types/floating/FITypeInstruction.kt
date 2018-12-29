package venusbackend.riscv.insts.dsl.types.floating

import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.extensions.FITypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.ITypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.extensions.b32.FITypeImplementation32
import venusbackend.riscv.insts.dsl.parsers.extensions.FITypeParser
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.Simulator

class FITypeInstruction(
    name: String,
    opcode: Int,
    funct3: Int,
    eval32: (Int, Simulator) -> Decimal
) : Instruction(
        name = name,
        format = ITypeFormat(opcode, funct3),
        parser = FITypeParser,
        impl16 = NoImplementation,
        impl32 = FITypeImplementation32(eval32),
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = FITypeDisassembler
)