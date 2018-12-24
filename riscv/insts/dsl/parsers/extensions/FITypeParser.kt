package venusbackend.riscv.insts.dsl.parsers.extensions

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber

/**
 * Created by thaum on 8/6/2018.
 */
object FITypeParser : InstructionParser {
    const val I_TYPE_MIN = -2048
    const val I_TYPE_MAX = 2047
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>) {
        checkArgsLength(args.size, 3)

        mcode[InstructionField.RD] = regNameToNumber(args[0], false)
        mcode[InstructionField.RS1] = regNameToNumber(args[2])
        mcode[InstructionField.IMM_11_0] = getImmediate(args[1], I_TYPE_MIN, I_TYPE_MAX)
    }
}