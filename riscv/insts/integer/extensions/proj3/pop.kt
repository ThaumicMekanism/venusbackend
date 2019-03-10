package venusbackend.riscv.insts.integer.extensions.proj3

import venusbackend.riscv.InstructionField
import venusbackend.riscv.Registers
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.OpcodeFunct3Format
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.base.ITypeParser.I_TYPE_MAX
import venusbackend.riscv.insts.dsl.parsers.base.ITypeParser.I_TYPE_MIN
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.types.Instruction

val pop = Instruction(
        name = "pop",
        format = OpcodeFunct3Format(0b0111011, 0b010),
        parser = RawParser { prog, mcode, args, dbg ->
            checkArgsLength(args.size, 1)
            mcode[InstructionField.IMM_11_0] = getImmediate(args[0], I_TYPE_MIN, I_TYPE_MAX)
        },
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val sp = sim.getReg(Registers.sp).toInt()
            val imm: Int = signExtend(mcode[InstructionField.IMM_11_0].toInt(), 12)
            sim.setReg(Registers.sp, sp + imm)
            sim.incrementPC(mcode.length)
        },
        impl64 = NoImplementation,
        impl128 = NoImplementation,
        disasm = RawDisassembler { mcode ->
            val imm = mcode[InstructionField.IMM_11_0]
            "pop $imm"
        }
)