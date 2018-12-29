package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.parsers.DoNothingParser

val fence = Instruction(
        name = "fence",
        format = InstructionFormat(4,
                listOf(FieldEqual(InstructionField.ENTIRE, 0b000000000000_00000_000_00000_0001111))
        ),
        parser = DoNothingParser,
        impl16 = NoImplementation,
        impl32 = NoImplementation,
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = RawDisassembler { "fence" }
)