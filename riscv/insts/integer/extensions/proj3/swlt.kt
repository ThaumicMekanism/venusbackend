package venusbackend.riscv.insts.integer.extensions.proj3

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.disasms.base.STypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.STypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.impls.base.b32.constructStoreImmediate
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.base.STypeParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
import venusbackend.riscv.insts.dsl.types.Instruction

val swlt = Instruction(
        name = "swlt",
        format = STypeFormat(0b0100011, 0b111),
        parser = RawParser { prog, mcode, args, dbg ->
            checkArgsLength(args.size, 3, dbg)
            var rs1 = args[2]

            if (rs1.startsWith("(") && rs1.endsWith(")")) {
                rs1 = rs1.substring(1, rs1.length - 1)
            }

            val imm = prog.getImmediate(args[1], STypeParser.S_TYPE_MIN, STypeParser.S_TYPE_MAX, dbg)
            mcode[InstructionField.RS1] = regNameToNumber(rs1, dbg = dbg)
            mcode[InstructionField.RS2] = regNameToNumber(args[0], dbg = dbg)
            mcode[InstructionField.IMM_4_0] = imm
            mcode[InstructionField.IMM_11_5] = imm shr 5
        },
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val rs1 = mcode[InstructionField.RS1].toInt()
            val rs2 = mcode[InstructionField.RS2].toInt()
            val imm = constructStoreImmediate(mcode)
            val vrs1 = sim.getReg(rs1).toInt()
            val vrs2 = sim.getReg(rs2).toInt()
            if (vrs2 < imm) {
                val address = vrs1
                val value = vrs2
                sim.storeWordwCache(address, value)
            }
            sim.incrementPC(mcode.length)
        },
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = STypeDisassembler
)