package venusbackend.riscv.insts.dsl.disasms

import venusbackend.riscv.MachineCode

class RawDisassembler(private val disasm: (MachineCode) -> String) : InstructionDisassembler {
    override fun invoke(mcode: MachineCode): String = disasm(mcode)
}
