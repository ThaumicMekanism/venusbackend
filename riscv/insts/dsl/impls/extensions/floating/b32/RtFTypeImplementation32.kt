package venusbackend.riscv.insts.dsl.impls.extensions.floating.b32

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.Simulator

class RtFTypeImplementation32(private val eval: (Int) -> Decimal) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1 = mcode[InstructionField.RS1].toInt()
        val rd = mcode[InstructionField.RD].toInt()
        val vrs1 = sim.getReg(rs1).toInt()
        sim.setFReg(rd, eval(vrs1))
        sim.incrementPC(mcode.length)
    }
}