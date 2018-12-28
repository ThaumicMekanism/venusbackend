package venusbackend.riscv.insts.dsl.impls.base

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.simulator.Simulator

class ShiftImmediateImplementation32(private val eval: (Int, Int) -> Int) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1 = mcode[InstructionField.RS1].toInt()
        val shamt = mcode[InstructionField.SHAMT].toInt()
        val rd = mcode[InstructionField.RD].toInt()
        val vrs1 = sim.getReg(rs1).toInt()
        sim.setReg(rd, eval(vrs1, shamt))
        sim.incrementPC(mcode.length)
    }
}
