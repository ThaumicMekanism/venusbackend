package venusbackend.riscv.insts.dsl.parsers.base

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber

object LoadParser : InstructionParser {
    const val I_TYPE_MIN = -2048
    const val I_TYPE_MAX = 2047
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 3)

//        val real_line = dbg.line.split(Regex("#")).firstOrNull()
//        if (real_line == null) {
//            AssemblerError("A dbg line was passed in but contained not code!")
//        }
//        // Do not need to check of other paren because the lexer will ensure no mismatch.
//        val numParen = real_line!!.count { c -> c == '(' }
//        if (numParen != 1) {
//            throw AssemblerError("Load takes only one set of parentheses around the last register!")
//        }

        mcode[InstructionField.RD] = regNameToNumber(args[0])
        mcode[InstructionField.RS1] = regNameToNumber(args[2])
        mcode[InstructionField.IMM_11_0] = prog.getImmediate(args[1], I_TYPE_MIN, I_TYPE_MAX)
    }
}
