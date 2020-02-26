package venusbackend.riscv.insts.integer.extensions.compressed.ci

import venusbackend.riscv.InstructionField
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

val caddi = Instruction(
        name = "c.addi",
        format = OpcodeCFunct3Format(0b01, 0b000, arrayOf(
                FieldEqual(InstructionField.RD, 0, true),
                FieldEqual(InstructionField.IMM_b2_b6, 0, true, listOf(InstructionField.IMM_b12))
        )),
        parser = RawParser { prog, mcode, args, dbg ->
            checkArgsLength(args.size, 2, dbg)

            mcode[InstructionField.RD] = regNameToNumber(args[0], dbg = dbg)
            val imm = getImmediate(args[1], (-(2.0).pow(6 - 1)).toInt(), (2.0).pow(6 - 1).toInt() - 1, dbg)
            val fimm = imm and 0b11111
            mcode[InstructionField.IMM_b2_b6] = fimm
            val simm = (imm and 0b100000) shr 5
            mcode[InstructionField.IMM_b12] = simm
        },
        impl16 = RawImplementation { mcode, sim ->
            throw InstructionNotSupportedError("C.ADDI is not supported by 16 bit systems!")
        },
        impl32 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val rawimm6_2 = mcode[InstructionField.IMM_b2_b6].toInt()
            val rawimm_12 = mcode[InstructionField.IMM_b12].toInt()
            val imm = ((rawimm_12) shl 5) or rawimm6_2
            val rs1v = sim.getReg(rd).toInt()
            sim.setReg(rd, imm + rs1v)
            sim.incrementPC(mcode.length)
        },
        impl64 = RawImplementation { mcode, sim ->
            throw InstructionNotSupportedError("C.ADDI is not supported by 64 bit systems!")
        },
        impl128 = RawImplementation { mcode, sim ->
            throw InstructionNotSupportedError("C.ADDI is not supported by 128 bit systems!")
        },
        disasm = RawDisassembler { mcode ->
            val rd = mcode[InstructionField.RD].toInt()
            val rawimm6_2 = mcode[InstructionField.IMM_b2_b6].toInt()
            val rawimm_12 = mcode[InstructionField.IMM_b12].toInt()
            val imm = ((rawimm_12) shl 5) or rawimm6_2
            "c.addi x$rd $imm"
        }
)