package venusbackend.riscv.insts.dsl.parsers

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program

object DoNothingParser : InstructionParser {
    const val B_TYPE_MIN = -4096
    const val B_TYPE_MAX = 4095
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 0, dbg)
    }
}
