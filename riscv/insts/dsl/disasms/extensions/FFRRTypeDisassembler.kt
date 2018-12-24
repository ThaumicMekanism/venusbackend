package venusbackend.riscv.insts.dsl.disasms.extensions

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.disasms.InstructionDisassembler

/**
 * Created by thaum on 8/6/2018.
 */
object FFRRTypeDisassembler : InstructionDisassembler {
    override fun invoke(mcode: MachineCode): String {
        val name = Instruction[mcode].name
        val rd = mcode[InstructionField.RD]
        val rs1 = mcode[InstructionField.RS1]
        val rs2 = mcode[InstructionField.RS2]
        return "$name x$rd f$rs1 f$rs2"
    }
}