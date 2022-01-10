package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.base.CSRITypeParser

/*
    TODO: 
    -> avoid side effects in case rs1 == x0 [Table 9.1 in "Volume I: RISC-V Unprivileged ISA V20191213"]
 */
val csrrci = Instruction(
        name = "csrrci",
        format = InstructionFormat(4,
                listOf(
                        FieldEqual(InstructionField.OPCODE, 0b1110011),
                        FieldEqual(InstructionField.FUNCT3, 0b111)
                )
        ),
        parser = CSRITypeParser,
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val imm = mcode[InstructionField.RS1].toInt()
            val vcsr = sim.getSReg(mcode[InstructionField.CSR]).toInt()
            sim.setReg(mcode[InstructionField.RD].toInt(), vcsr)
            sim.setSReg(mcode[InstructionField.CSR], imm.inv() and vcsr)
            sim.incrementPC(mcode.length)
        },
        impl64 = RawImplementation { mcode, sim ->
            val imm = mcode[InstructionField.RS1].toLong()
            val vcsr = sim.getSReg(mcode[InstructionField.CSR]).toLong()
            sim.setReg(mcode[InstructionField.RD].toInt(), vcsr)
            sim.setSReg(mcode[InstructionField.CSR], imm.inv() and vcsr)
            sim.incrementPC(mcode.length)
        },
        impl128 = RawImplementation { mcode, sim ->
            val imm = mcode[InstructionField.RS1].toQuadWord()
            val vcsr = sim.getSReg(mcode[InstructionField.CSR]).toQuadWord()
            sim.setReg(mcode[InstructionField.RD].toInt(), vcsr)
            sim.setSReg(mcode[InstructionField.CSR], imm.inv() and vcsr)
            sim.incrementPC(mcode.length)
        },
        disasm = RawDisassembler {
            val dest = it[InstructionField.RD]
            val source = it[InstructionField.RS1]
            val csr = it[InstructionField.CSR]
            "csrrci x$dest $csr $source"
        }
)