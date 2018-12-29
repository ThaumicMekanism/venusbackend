package venusbackend.riscv.insts.dsl.disasms.extensions

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.InstructionDisassembler

/**
 * Created by thaum on 8/6/2018.
 */
object FRRTypeDisassembler : InstructionDisassembler {
    override fun invoke(mcode: MachineCode): String {
        val name = Instruction[mcode].name
        val rd = mcode[InstructionField.RD]
        val rs1 = mcode[InstructionField.RS1]
        return "$name x$rd f$rs1"
    }
}