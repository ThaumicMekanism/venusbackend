package venusbackend.riscv.insts.integer.extensions.compressed.ci

import venusbackend.riscv.InstructionField
import venusbackend.riscv.Registers
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.OpcodeCFunct3Format
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
import venusbackend.riscv.insts.dsl.types.Instruction
import kotlin.math.pow

val clwsp = Instruction(
        name = "c.lwsp",
        format = OpcodeCFunct3Format(0b10, 0b010, arrayOf(
                FieldEqual(InstructionField.RD, 0, true)
        )),
        parser = RawParser { prog, mcode, args, dbg ->
            checkArgsLength(args.size, 2, dbg)

            mcode[InstructionField.RD] = regNameToNumber(args[0], dbg = dbg)
            val imm = getImmediate(args[1], (-(2.0).pow(8 - 1)).toInt(), (2.0).pow(8 - 1).toInt() - 1, dbg)
            throw NotImplementedError("Still working on correct imm for C.LWSP")
//            mcode[InstructionField.IMM_b2_b6] = (imm and 0b1111100) shr 2
//            mcode[InstructionField.IMM_b12] = (imm shr 7) and 0b1
        },
        impl16 = RawImplementation { mcode, sim ->
            throw InstructionNotSupportedError("C.LWSP is not supported by 16 bit systems!")
        },
        impl32 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rawimm6_2 = mcode[InstructionField.IMM_b2_b6].toInt()
            val rawimm_12 = mcode[InstructionField.IMM_b12].toInt()
            val imm4_2 = rawimm6_2 and 0b11100
            val imm7_6 = rawimm6_2 and 0b00011
            val imm = ((imm4_2 or (rawimm_12 shl 3) or (imm7_6 shl 4)) shl 2)
            val sp = sim.getReg(Registers.sp).toInt()
            val data = sim.loadWordwCache(sp + imm)
            sim.setReg(rd, data)
            sim.incrementPC(mcode.length)
        },
        impl64 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rawimm6_2 = mcode[InstructionField.IMM_b2_b6].toInt()
            val rawimm_12 = mcode[InstructionField.IMM_b12].toInt()
            val imm4_2 = rawimm6_2 and 0b11100
            val imm7_6 = rawimm6_2 and 0b00011
            val imm = ((imm4_2 or (rawimm_12 shl 3) or (imm7_6 shl 4)) shl 2)
            val sp = sim.getReg(Registers.sp).toLong()
            val data = sim.loadWordwCache(sp + imm).toLong()
            sim.setReg(rd, data)
            sim.incrementPC(mcode.length)
        },
        impl128 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rawimm6_2 = mcode[InstructionField.IMM_b2_b6].toInt()
            val rawimm_12 = mcode[InstructionField.IMM_b12].toInt()
            val imm4_2 = rawimm6_2 and 0b11100
            val imm7_6 = rawimm6_2 and 0b00011
            val imm = ((imm4_2 or (rawimm_12 shl 3) or (imm7_6 shl 4)) shl 2)
            val sp = sim.getReg(Registers.sp).toLong()
            val data = sim.loadWordwCache(sp + imm).toLong()
            sim.setReg(rd, data)
            sim.incrementPC(mcode.length)
        },
        disasm = RawDisassembler { mcode ->
            val rd = mcode[InstructionField.RD].toInt()
            val rawimm6_2 = mcode[InstructionField.IMM_b2_b6].toInt()
            val rawimm_12 = mcode[InstructionField.IMM_b12].toInt()
            val imm4_2 = rawimm6_2 and 0b11100
            val imm7_6 = rawimm6_2 and 0b00011
            val imm = ((imm4_2 or (rawimm_12 shl 3) or (imm7_6 shl 4)) shl 2)
            "c.lwsp x$rd $imm"
        }
)