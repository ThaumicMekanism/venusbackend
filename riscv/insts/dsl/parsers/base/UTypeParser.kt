package venusbackend.riscv.insts.dsl.parsers.base

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
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>) {
        checkArgsLength(args.size, 2)

        mcode[InstructionField.RD] = regNameToNumber(args[0])
        mcode[InstructionField.IMM_31_12] = getImmediate(args[1], U_TYPE_MIN, U_TYPE_MAX)
    }
}
