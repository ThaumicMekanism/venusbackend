package venusbackend.riscv.insts.dsl.parsers

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program

class RawParser(private val eval: (Program, MachineCode, List<String>, DebugInfo) -> Unit) : InstructionParser {
    override fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) = eval(prog, mcode, args, dbg)
}
