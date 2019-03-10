package venusbackend.riscv.insts.integer.extensions.proj3

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.Registers
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.OpcodeFunct3Format
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.types.Instruction

val pop = Instruction(
        name = "pop",
        format = OpcodeFunct3Format(0b0111011, 0b100),
        parser = RawParser { prog, mcode, args, dbg ->
            checkArgsLength(args.size, 0)
        },
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val sp = sim.getReg(Registers.sp).toInt()
            sim.setReg(Registers.sp, sp + 4)
            sim.incrementPC(mcode.length)
        },
        impl64 = RawImplementation { mcode, sim ->
            val sp = sim.getReg(Registers.sp).toLong()
            sim.setReg(Registers.sp, sp + 8)
            sim.incrementPC(mcode.length)
        },
        impl128 = RawImplementation { mcode, sim ->
            val sp = sim.getReg(Registers.sp).toQuadWord()
            sim.setReg(Registers.sp, sp + 16)
            sim.incrementPC(mcode.length)
        },
        disasm = RawDisassembler { mcode ->
            "pop"
        }
)