package venusbackend.riscv.insts.integer.extensions.proj3

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.disasms.base.RTypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.RTypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.base.RTypeParser
import venusbackend.riscv.insts.dsl.types.Instruction

val vaddh = Instruction(
        name = "vaddh",
        format = RTypeFormat(0x33, 0x0, 0x0F),
        parser = RTypeParser,
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val rs1 = sim.getReg(mcode[InstructionField.RS1]).toInt()
            val rs2 = sim.getReg(mcode[InstructionField.RS2]).toInt()
            val upper = (rs1 shr 16).toShort() + (rs2 shr 16).toShort()
            val lower = (rs1.toShort() + rs2.toShort()).toShort().toInt()
            val rd = (upper shl 16) or (lower and 0xFFFF)
            sim.setReg(mcode[InstructionField.RD], rd)
            sim.incrementPC(mcode.length)
        },
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = RTypeDisassembler
)