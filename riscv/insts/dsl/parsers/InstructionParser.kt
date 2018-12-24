package venusbackend.riscv.insts.dsl.parsers

import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program

interface InstructionParser {
    operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>)
}
