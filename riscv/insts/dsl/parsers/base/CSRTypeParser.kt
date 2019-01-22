package venusbackend.riscv.insts.dsl.parsers.base

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber

object CSRTypeParser : InstructionParser {
    const val I_TYPE_MIN = -2048
    const val I_TYPE_MAX = 2047
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 3)

        mcode[InstructionField.RD] = regNameToNumber(args[0])
        mcode[InstructionField.RS1] = regNameToNumber(args[2])
        mcode[InstructionField.IMM_11_0] = getImmediate(args[1], CSRTypeParser.I_TYPE_MIN, CSRTypeParser.I_TYPE_MAX)
    }
}