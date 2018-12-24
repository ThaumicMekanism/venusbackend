package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.DoNothingParser

val ebreak = Instruction(
        name = "ebreak",
        format = InstructionFormat(4,
                listOf(FieldEqual(InstructionField.ENTIRE, 0b000000000001_00000_000_00000_1110011))
        ),
        parser = DoNothingParser,
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            sim.ebreak = true
            sim.incrementPC(mcode.length)
        },
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = RawDisassembler { "ebreak" }
)