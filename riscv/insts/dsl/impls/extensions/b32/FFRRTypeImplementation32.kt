package venusbackend.riscv.insts.dsl.impls.extensions.b32

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.Simulator

/**
 * Created by thaum on 8/6/2018.
 */
class FFRRTypeImplementation32(private val eval: (Decimal, Decimal) -> Int) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1 = mcode[InstructionField.RS1].toInt()
        val rs2 = mcode[InstructionField.RS2].toInt()
        val rd = mcode[InstructionField.RD].toInt()
        val vrs1 = sim.getFReg(rs1)
        val vrs2 = sim.getFReg(rs2)
        sim.setReg(rd, eval(vrs1, vrs2))
        sim.incrementPC(mcode.length)
    }
}