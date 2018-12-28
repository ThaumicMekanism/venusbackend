package venusbackend.riscv.insts.dsl.impls.base

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.simulator.Simulator

class RTypeImplementation64(private val eval: (Long, Long) -> Long) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1 = mcode[InstructionField.RS1].toInt()
        val rs2 = mcode[InstructionField.RS2].toInt()
        val rd = mcode[InstructionField.RD].toInt()
        val vrs1 = sim.getReg(rs1).toLong()
        val vrs2 = sim.getReg(rs2).toLong()
        // TODO not do conversions. This fix will require a lot of base changes. Most likely will use BigInt.
        sim.setReg(rd, eval(vrs1, vrs2).toInt())
        sim.incrementPC(mcode.length)
    }
}
