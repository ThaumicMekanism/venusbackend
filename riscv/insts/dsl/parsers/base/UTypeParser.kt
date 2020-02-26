package venusbackend.riscv.insts.dsl.parsers.base

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber

object UTypeParser : InstructionParser {
    const val U_TYPE_MIN = 0
    const val U_TYPE_MAX = 1048575
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 2, dbg)

        mcode[InstructionField.RD] = regNameToNumber(args[0], dbg = dbg)
        mcode[InstructionField.IMM_31_12] = prog.getImmediate(args[1], U_TYPE_MIN, U_TYPE_MAX, dbg)
    }
}
