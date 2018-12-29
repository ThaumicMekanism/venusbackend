package venusbackend.riscv.insts.integer.extensions.compressed.ci

import venusbackend.riscv.InstructionField
import venusbackend.riscv.Registers
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.OpcodeCFunct3Format
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
import venusbackend.riscv.insts.dsl.types.Instruction

val clwsp = Instruction(
        name = "c.lwsp",
        format = OpcodeCFunct3Format(0b10, 0b010),
        parser = RawParser { prog, mcode, args ->
            checkArgsLength(args.size, 2)

            mcode[InstructionField.RD] = regNameToNumber(args[0])
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
            val data = sim.loadWordwCache(Registers.sp + imm)
            sim.setReg(rd, data)
        },
        impl64 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rawimm6_2 = mcode[InstructionField.IMM_b2_b6].toInt()
            val rawimm_12 = mcode[InstructionField.IMM_b12].toInt()
            val imm4_2 = rawimm6_2 and 0b11100
            val imm7_6 = rawimm6_2 and 0b00011
            val imm = ((imm4_2 or (rawimm_12 shl 3) or (imm7_6 shl 4)) shl 2)
            val data = sim.loadWordwCache(Registers.sp + imm).toLong()
            sim.setReg(rd, data)
        },
        impl128 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rawimm6_2 = mcode[InstructionField.IMM_b2_b6].toInt()
            val rawimm_12 = mcode[InstructionField.IMM_b12].toInt()
            val imm4_2 = rawimm6_2 and 0b11100
            val imm7_6 = rawimm6_2 and 0b00011
            val imm = ((imm4_2 or (rawimm_12 shl 3) or (imm7_6 shl 4)) shl 2)
            val data = sim.loadWordwCache(Registers.sp + imm).toLong()
            sim.setReg(rd, data)
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