package venusbackend.riscv.insts.dsl.parsers.base

import venusbackend.assembler.AssemblerError
import venusbackend.assembler.DebugInfo
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
import venusbackend.riscv.insts.dsl.relocators.ImmAbsRelocator
import venusbackend.riscv.isNumeral

object ITypeParser : InstructionParser {
    const val I_TYPE_MIN = -2048
    const val I_TYPE_MAX = 2047
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 3, dbg)

        val real_line = dbg.line.split(Regex("#")).firstOrNull()
        // Do not need to check of other paren because the lexer will ensure no mismatch.
        if (real_line != null && real_line.contains(Regex("[(]"))) {
            throw AssemblerError("I-Type Instructions should not be in Displaced Notation!", dbg)
        }

        mcode[InstructionField.RD] = regNameToNumber(args[0], dbg = dbg)
        mcode[InstructionField.RS1] = regNameToNumber(args[1], dbg = dbg)
        if (isNumeral(args[2])) {
            mcode[InstructionField.IMM_11_0] =
                    prog.getImmediate(args[2], I_TYPE_MIN, I_TYPE_MAX, dbg)
        } else {
            prog.addRelocation(
                    ImmAbsRelocator, prog.symbolPart(args[2], dbg),
                    prog.labelOffsetPart(args[2], dbg), dbg = dbg)
        }
    }
}
