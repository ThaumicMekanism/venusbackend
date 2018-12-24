package venusbackend.riscv.insts.dsl.impls.base

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.simulator.Simulator

class ITypeImplementation32(private val eval: (Int, Int) -> Int) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1: Int = mcode[InstructionField.RS1]
        val imm: Int = signExtend(mcode[InstructionField.IMM_11_0], 12)
        val rd: Int = mcode[InstructionField.RD]
        val vrs1: Int = sim.getReg(rs1)
        sim.setReg(rd, eval(vrs1, imm))
        sim.incrementPC(mcode.length)
    }
}
