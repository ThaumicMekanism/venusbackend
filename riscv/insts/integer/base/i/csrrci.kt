package venusbackend.riscv.insts.integer.base.i

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.base.CSRTypeParser

val csrrci = Instruction(
        name = "csrrci",
        format = InstructionFormat(4,
                listOf(
                        FieldEqual(InstructionField.OPCODE, 0b1110011),
                        FieldEqual(InstructionField.FUNCT3, 0b111)
                )
        ),
        parser = CSRTypeParser,
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val imm = mcode[InstructionField.RS1]
            val vcsr = sim.getReg(32)
            sim.setReg(mcode[InstructionField.RD], vcsr)
            sim.setReg(32, imm.inv() and vcsr)
            sim.incrementPC(mcode.length)
        },
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = RawDisassembler {
            val dest = it[InstructionField.RD]
            val source = it[InstructionField.RS1]
            val csr = it[InstructionField.IMM_11_0]
            "csrrci x$dest $csr $source"
        }
)