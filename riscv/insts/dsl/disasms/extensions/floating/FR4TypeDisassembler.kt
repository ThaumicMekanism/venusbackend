package venusbackend.riscv.insts.dsl.disasms.extensions.floating

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.InstructionDisassembler

/**
 * Created by thaum on 8/6/2018.
 */
object FR4TypeDisassembler : InstructionDisassembler {
    override fun invoke(mcode: MachineCode): String {
        val name = Instruction[mcode].name
        val rd = mcode[InstructionField.RD]
        val rs1 = mcode[InstructionField.RS1]
        val rs2 = mcode[InstructionField.RS2]
        val rs3 = mcode[InstructionField.RS3]
        return "$name f$rd f$rs1 f$rs2 f$rs3"
    }
}