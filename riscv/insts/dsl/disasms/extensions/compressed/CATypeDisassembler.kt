package venusbackend.riscv.insts.dsl.disasms.extensions.compressed

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.InstructionDisassembler

object CATypeDisassembler : InstructionDisassembler {
    override fun invoke(mcode: MachineCode): String {
        val name = Instruction[mcode].name
        val rd = mcode[InstructionField.RDP] + 8
        val rs2 = mcode[InstructionField.RS2P] + 8
        return "$name x$rd x$rs2"
    }
}
