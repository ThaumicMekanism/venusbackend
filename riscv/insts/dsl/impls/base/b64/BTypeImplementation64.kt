package venusbackend.riscv.insts.dsl.impls.base.b64

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.riscv.insts.dsl.impls.setBitslice
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.simulator.Simulator

class BTypeImplementation64(private val cond: (Long, Long) -> Boolean) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) {
        val rs1: Int = mcode[InstructionField.RS1].toInt()
        val rs2: Int = mcode[InstructionField.RS2].toInt()
        val imm: Long = constructBranchImmediate64(mcode)
        val vrs1: Long = sim.getReg(rs1).toLong()
        val vrs2: Long = sim.getReg(rs2).toLong()
        if (cond(vrs1, vrs2)) {
            sim.branched = true
            sim.incrementPC(imm)
        } else {
            sim.incrementPC(mcode.length)
        }
    }
}

fun constructBranchImmediate64(mcode: MachineCode): Long {
    val imm_11 = mcode[InstructionField.IMM_11_B].toInt()
    val imm_4_1 = mcode[InstructionField.IMM_4_1].toInt()
    val imm_10_5 = mcode[InstructionField.IMM_10_5].toInt()
    val imm_12 = mcode[InstructionField.IMM_12].toInt()
    var imm = 0
    imm = setBitslice(imm, imm_11, 11, 12)
    imm = setBitslice(imm, imm_4_1, 1, 5)
    imm = setBitslice(imm, imm_10_5, 5, 11)
    imm = setBitslice(imm, imm_12, 12, 13)
    return signExtend(imm, 13).toLong()
}
