package venusbackend.riscv.insts.integer.base.i

import venusbackend.assembler.AssemblerError
import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.base.ITypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.riscv.insts.dsl.parsers.base.ITypeParser
import venusbackend.riscv.insts.dsl.parsers.base.LoadParser
import venusbackend.riscv.insts.dsl.parsers.RawParser

val jalr = Instruction(
        name = "jalr",
        format = ITypeFormat(
                opcode = 0b1100111,
                funct3 = 0b000
        ),
        parser = RawParser { prog, mcode, args ->
            try {
                ITypeParser(prog, mcode, args)
            } catch (e: AssemblerError) {
                /* Try base displacement notation */
                try {
                    LoadParser(prog, mcode, args)
                } catch (e_two: AssemblerError) {
                    throw e
                }
            }
        },
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rs1 = mcode[InstructionField.RS1].toInt()
            val imm = signExtend(mcode[InstructionField.IMM_11_0].toInt(), 12)
            val vrs1 = sim.getReg(rs1).toInt()
            sim.setReg(rd, sim.getPC().toInt() + mcode.length)
            sim.setPC(((vrs1 + imm) shr 1) shl 1)
            sim.jumped = true
        },
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = RawDisassembler { mcode ->
            val rd = mcode[InstructionField.RD]
            val rs1 = mcode[InstructionField.RS1]
            val imm = signExtend(mcode[InstructionField.IMM_11_0].toInt(), 12)
            "jalr x$rd x$rs1 $imm"
        }
)
