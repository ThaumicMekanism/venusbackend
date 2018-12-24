package venusbackend.riscv.insts.dsl.disasms

import venusbackend.riscv.MachineCode

interface InstructionDisassembler {
    operator fun invoke(mcode: MachineCode): String
}
