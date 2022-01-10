package venusbackend.riscv.insts.dsl.impls

import venusbackend.and
import venusbackend.or
import venusbackend.riscv.MachineCode
import venusbackend.shl
import venusbackend.shr
import venusbackend.simulator.Simulator
import venusbackend.simulator.SpecialRegisters

class TRTypeImplementation: InstructionImplementation {
    override fun invoke(mcode: MachineCode, sim: Simulator) {
        val last7BitsOfMstatus = sim.getSReg(SpecialRegisters.MSTATUS.address) and 0x7F // Save last 7 bits
        val mpieBit = sim.getSReg(SpecialRegisters.MSTATUS.address) and 0x80 // Take only mpie bit from mstatus
        val mieBitMask = mpieBit shr 4 // build the mask for the mie bit in mstatus
        /**
         * Zeroes out the last 8 bits then recreates the last 7 bits (just making sure the mpie bit is 0)
         * Then sets the mie bit to the value that was in mpie.
         */
        val mstatusNewValue = (((((sim.getSReg(SpecialRegisters.MSTATUS.address) shr 8) shl 8) or last7BitsOfMstatus) or mieBitMask))
        sim.setSReg(SpecialRegisters.MSTATUS.address, mstatusNewValue)
        // mpp bit in mstatus will not be set because currently there's only machine mode
        // TODO: 
        val newPC = sim.getSReg(SpecialRegisters.MEPC.address)
        sim.setPC(newPC.toInt())
    }
}