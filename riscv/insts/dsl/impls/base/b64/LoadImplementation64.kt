package venusbackend.riscv.insts.dsl.impls.base.b64

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.simulator.Simulator

class LoadImplementation64(
    private val load: (Simulator, Long) -> Long,
    private val postLoad: (Long) -> Long
) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1 = mcode[InstructionField.RS1].toInt()
        val rd = mcode[InstructionField.RD].toInt()
        val vrs1 = sim.getReg(rs1).toLong()
        val imm = signExtend(mcode[InstructionField.IMM_11_0].toInt(), 12).toLong()
        sim.setReg(rd, postLoad(load(sim, vrs1 + imm)))
        sim.incrementPC(mcode.length)
    }
}
