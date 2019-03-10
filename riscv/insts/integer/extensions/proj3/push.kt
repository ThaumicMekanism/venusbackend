package venusbackend.riscv.insts.integer.extensions.proj3

import venusbackend.riscv.InstructionField
import venusbackend.riscv.Registers
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.OpcodeFunct3Format
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
import venusbackend.riscv.insts.dsl.types.Instruction

val push = Instruction(
        name = "push",
        format = OpcodeFunct3Format(0b0111011, 0b011),
        parser = RawParser { prog, mcode, args, dbg ->
            checkArgsLength(args.size, 1)
            mcode[InstructionField.RS2] = regNameToNumber(args[0])
        },
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val rs2 = mcode[InstructionField.RS2].toInt()
            val vrs2 = sim.getReg(rs2).toInt()
            var vsp = sim.getReg(Registers.sp).toInt()
            vsp -= 4
            sim.setReg(Registers.sp, vsp)
            sim.storeWordwCache(vsp, vrs2)
            sim.incrementPC(mcode.length)
        },
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = RawDisassembler { mcode ->
            val rs2 = mcode[InstructionField.RS2]
            "push x$rs2"
        }
)