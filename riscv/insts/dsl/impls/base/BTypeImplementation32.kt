package venusbackend.riscv.insts.dsl.impls.base

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.riscv.insts.dsl.impls.setBitslice
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.simulator.Simulator

class BTypeImplementation32(private val cond: (Int, Int) -> Boolean) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1: Int = mcode[InstructionField.RS1].toInt()
        val rs2: Int = mcode[InstructionField.RS2].toInt()
        val imm: Int = constructBranchImmediate(mcode)
        val vrs1: Int = sim.getReg(rs1).toInt()
        val vrs2: Int = sim.getReg(rs2).toInt()
        if (cond(vrs1, vrs2)) {
            sim.branched = true
            sim.incrementPC(imm)
        } else {
            sim.incrementPC(mcode.length)
        }
    }
}

fun constructBranchImmediate(mcode: MachineCode): Int {
    val imm_11 = mcode[InstructionField.IMM_11_B].toInt()
    val imm_4_1 = mcode[InstructionField.IMM_4_1].toInt()
    val imm_10_5 = mcode[InstructionField.IMM_10_5].toInt()
    val imm_12 = mcode[InstructionField.IMM_12].toInt()
    var imm = 0
    imm = setBitslice(imm, imm_11, 11, 12)
    imm = setBitslice(imm, imm_4_1, 1, 5)
    imm = setBitslice(imm, imm_10_5, 5, 11)
    imm = setBitslice(imm, imm_12, 12, 13)
    return signExtend(imm, 13)
}
