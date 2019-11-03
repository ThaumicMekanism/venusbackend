package venusbackend.riscv.insts.integer.extensions.compressed.ci

import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.InstructionNotSupportedError
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.OpcodeCFunct3Format
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.types.Instruction

val cnop = Instruction(
        name = "c.nop",
        format = OpcodeCFunct3Format(0b01, 0b000, arrayOf(
                FieldEqual(InstructionField.RD, 0, false),
                FieldEqual(InstructionField.IMM_b2_b6, 0, false, listOf(InstructionField.IMM_b12))
        )),
        parser = RawParser { prog, mcode, args, dbg ->
            checkArgsLength(args.size, 0)
        },
        impl16 = RawImplementation { mcode, sim ->
            throw InstructionNotSupportedError("C.NOP is not supported by 16 bit systems!")
        },
        impl32 = RawImplementation { mcode, sim ->
            sim.incrementPC(mcode.length)
        },
        impl64 = RawImplementation { mcode, sim ->
            throw InstructionNotSupportedError("C.NOP is not supported by 64 bit systems!")
        },
        impl128 = RawImplementation { mcode, sim ->
            throw InstructionNotSupportedError("C.NOP is not supported by 128 bit systems!")
        },
        disasm = RawDisassembler { mcode ->
            "c.nop"
        }
)