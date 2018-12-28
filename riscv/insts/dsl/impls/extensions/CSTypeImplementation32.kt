package venusbackend.riscv.insts.dsl.impls.extensions

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.simulator.Simulator

class CSTypeImplementation32(private val eval: (Int, Int) -> Int) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1 = mcode[InstructionField.RS1P].toInt() + 8 // Add 8 because the CS instruction use the popular registers (8-15)
        val rs2 = mcode[InstructionField.RS2P].toInt() + 8
        val rd = mcode[InstructionField.RDP].toInt() + 8
        val vrs1 = sim.getReg(rs1).toInt()
        val vrs2 = sim.getReg(rs2).toInt()

        sim.setReg(rd, eval(vrs1, vrs2))
        sim.incrementPC(mcode.length)
    }
}