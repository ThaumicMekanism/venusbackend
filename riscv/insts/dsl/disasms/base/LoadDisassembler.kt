package venusbackend.riscv.insts.dsl.disasms.base

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.InstructionDisassembler
import venusbackend.riscv.insts.dsl.impls.signExtend

object LoadDisassembler : InstructionDisassembler {
    override fun invoke(mcode: MachineCode): String {
        val name = Instruction[mcode].name
        val rs1 = mcode[InstructionField.RS1]
        val rd = mcode[InstructionField.RD]
        val imm = signExtend(mcode[InstructionField.IMM_11_0].toInt(), 12)
        return "$name x$rd $imm(x$rs1)"
    }
}
