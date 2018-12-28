package venusbackend.riscv.insts.dsl.impls.base.b64

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.simulator.Simulator

class ShiftImmediateImplementation64(private val eval: (Long, Long) -> Long) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1 = mcode[InstructionField.RS1].toInt()
        val shamt = mcode[InstructionField.SHAMT].toLong()
        val rd = mcode[InstructionField.RD].toInt()
        val vrs1 = sim.getReg(rs1).toLong()
        sim.setReg(rd, eval(vrs1, shamt))
        sim.incrementPC(mcode.length)
    }
}
