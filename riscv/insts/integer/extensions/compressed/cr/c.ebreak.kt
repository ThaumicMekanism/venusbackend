package venusbackend.riscv.insts.integer.extensions.compressed.cr

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat
import venusbackend.riscv.insts.dsl.parsers.DoNothingParser
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.integer.base.i.ebreak

val cebreak = Instruction(
        name = "ebreak",
        format = InstructionFormat(2,
                listOf(FieldEqual(InstructionField.HALF, 0b100_1_00000_00000_10))
        ),
        parser = DoNothingParser,
        impl16 = ebreak.impl16,
        impl32 = ebreak.impl32,
        impl64 = ebreak.impl64,
        impl128 = ebreak.impl128,
        disasm = RawDisassembler { "c.ebreak" }
)