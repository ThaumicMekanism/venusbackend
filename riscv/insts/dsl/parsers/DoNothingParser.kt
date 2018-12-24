package venusbackend.riscv.insts.dsl.parsers

import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program

object DoNothingParser : InstructionParser {
    const val B_TYPE_MIN = -2048
    const val B_TYPE_MAX = 2047
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>) {
        checkArgsLength(args.size, 0)
    }
}
