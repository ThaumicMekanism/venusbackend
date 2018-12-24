package venusbackend.riscv.insts.dsl.disasms.base

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.disasms.InstructionDisassembler
import venusbackend.riscv.insts.dsl.impls.base.constructBranchImmediate

object BTypeDisassembler : InstructionDisassembler {
    override fun invoke(mcode: MachineCode): String {
        val name = Instruction[mcode].name
        val rs1 = mcode[InstructionField.RS1]
        val rs2 = mcode[InstructionField.RS2]
        val imm = constructBranchImmediate(mcode)
        return "$name x$rs1 x$rs2 $imm"
    }
}
