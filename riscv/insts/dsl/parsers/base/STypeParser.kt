package venusbackend.riscv.insts.dsl.parsers.base

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber

object STypeParser : InstructionParser {
    const val S_TYPE_MIN = -2048
    const val S_TYPE_MAX = 2047
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 3)

//        val real_line = dbg.line.split(Regex("#")).firstOrNull()
//        // Do not need to check of other paren because the lexer will ensure no mismatch.
//        if (real_line != null && real_line.count { c -> c == '(' } == 1) {
//            throw AssemblerError("Store takes only one set of parentheses around the last register!")
//        }

        val imm = getImmediate(args[1], S_TYPE_MIN, S_TYPE_MAX)
        mcode[InstructionField.RS1] = regNameToNumber(args[2])
        mcode[InstructionField.RS2] = regNameToNumber(args[0])
        mcode[InstructionField.IMM_4_0] = imm
        mcode[InstructionField.IMM_11_5] = imm shr 5
    }
}
