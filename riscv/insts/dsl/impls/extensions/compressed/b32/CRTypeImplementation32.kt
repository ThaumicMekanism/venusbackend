package venusbackend.riscv.insts.dsl.impls.extensions.compressed.b32

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.simulator.Simulator

class CRTypeImplementation32(private val eval: (Int, Int) -> Int) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs2 = mcode[InstructionField.CRS2].toInt()
        val rd = mcode[InstructionField.RD].toInt()
        val vrs1 = sim.getReg(rd).toInt()
        val vrs2 = sim.getReg(rs2).toInt()

        sim.setReg(rd, eval(vrs1, vrs2))
        sim.incrementPC(mcode.length)
    }
}