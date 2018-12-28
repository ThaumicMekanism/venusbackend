package venusbackend.riscv.insts.dsl.impls.base.b64

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.simulator.Simulator

class ITypeImplementation64(private val eval: (Long, Long) -> Long) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1: Int = mcode[InstructionField.RS1].toInt()
        val imm: Long = signExtend(mcode[InstructionField.IMM_11_0].toInt(), 12).toLong()
        val rd: Int = mcode[InstructionField.RD].toInt()
        val vrs1: Long = sim.getReg(rs1).toLong()
        sim.setReg(rd, eval(vrs1, imm))
        sim.incrementPC(mcode.length)
    }
}
