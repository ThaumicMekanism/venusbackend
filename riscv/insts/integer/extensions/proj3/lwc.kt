package venusbackend.riscv.insts.integer.extensions.proj3

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.OpcodeFunct3Format
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
import venusbackend.riscv.insts.dsl.types.Instruction

val lwc = Instruction(
        name = "lwc",
        format = OpcodeFunct3Format(0b0111011, 0b010),
        parser = RawParser { prog, mcode, args, dbg ->
            checkArgsLength(args.size, 4)

            mcode[InstructionField.RD] = regNameToNumber(args[0])
            mcode[InstructionField.FUNCT7] = getImmediate(args[1], -64, 63)
            mcode[InstructionField.RS1] = regNameToNumber(args[2])
            mcode[InstructionField.RS2] = regNameToNumber(args[3])
        },
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rs1 = mcode[InstructionField.RS1].toInt()
            val rs2 = mcode[InstructionField.RS2].toInt()
            val funct7 = mcode[InstructionField.FUNCT7].toInt()
            val vrs1 = sim.getReg(rs1).toInt()
            val vrs2 = sim.getReg(rs2).toInt()
            if (vrs2 != 0) {
                val addr = funct7 + vrs1
                val m = sim.loadWordwCache(addr)
                sim.setReg(rd, m)
            }
            sim.incrementPC(mcode.length)
        },
        impl64 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rs1 = mcode[InstructionField.RS1].toInt()
            val rs2 = mcode[InstructionField.RS2].toInt()
            val funct7 = mcode[InstructionField.FUNCT7].toInt()
            val vrs1 = sim.getReg(rs1).toLong()
            val vrs2 = sim.getReg(rs2).toLong()
            if (vrs2 != 0L) {
                val addr = funct7 + vrs1
                val m = sim.loadWordwCache(addr)
                sim.setReg(rd, m.toLong())
            }
            sim.incrementPC(mcode.length)
        },
        impl128 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rs1 = mcode[InstructionField.RS1].toInt()
            val rs2 = mcode[InstructionField.RS2].toInt()
            val funct7 = mcode[InstructionField.FUNCT7].toQuadWord()
            val vrs1 = sim.getReg(rs1).toQuadWord()
            val vrs2 = sim.getReg(rs2).toQuadWord()
            if (vrs2 != 0.toQuadWord()) {
                val addr = funct7 + vrs1
                val m = sim.loadWordwCache(addr)
                sim.setReg(rd, m.toQuadWord())
            }
            sim.incrementPC(mcode.length)
        },
        disasm = RawDisassembler { mcode ->
            val rd = mcode[InstructionField.RD]
            val rs1 = mcode[InstructionField.RS1]
            val rs2 = mcode[InstructionField.RS2]
            val funct7 = mcode[InstructionField.FUNCT7]
            "lwc x$rd $funct7(x$rs1) x$rs2"
        }
)