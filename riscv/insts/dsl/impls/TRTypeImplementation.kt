package venusbackend.riscv.insts.dsl.impls

import venusbackend.and
import venusbackend.or
import venusbackend.riscv.MachineCode
import venusbackend.shl
import venusbackend.shr
import venusbackend.simulator.Simulator
import venusbackend.simulator.SpecialRegisters

import venusbackend.simulator.SimulatorError

class TRTypeImplementation: InstructionImplementation {
    override fun invoke(mcode: MachineCode, sim: Simulator) {

        // check if current PRIV level allows for mret instruction
        if( sim.getPRIV().toInt() < 3) {
            //throw SimulatorError("MRET ony allowed in M-mode! => illegal instruction exception")
            sim.setSReg(SpecialRegisters.MCAUSE.address, 2) // exception code: 'illegal instruction'
            sim.handleMachineException()
        }
        else {
            /*
            * for details see: 
            * Volume II: RISC-V Privileged Architectures V20190608-Priv-MSU-Ratifi ed
            * 3.1.6.1 Privilege and Global Interrupt-Enable Stack in mstatus register
            * page 21
            */

            /* mie is set to mpie (mstatsus.mip <-- mstatus.mpie) */
            val mieBitMask = (sim.getSReg(SpecialRegisters.MSTATUS.address) and 0x80) shr 4 // get mpie and build mask for the mie
            val mstatusNewValue = ((sim.getSReg(SpecialRegisters.MSTATUS.address) and (-1 xor (1 shl 3))) or mieBitMask)
            sim.setSReg(SpecialRegisters.MSTATUS.address, mstatusNewValue)       

            /* mpie is set to 1 (mstatus.mpie <-- b1) */
            sim.setSReg(SpecialRegisters.MSTATUS.address, (sim.getSReg(SpecialRegisters.MSTATUS.address) or 0x80))

            /* change privileged mode of hart to mpp (PRIV <-- mstatus.mpp) */
            val mpp = (sim.getSReg(SpecialRegisters.MSTATUS.address) shr 11) and 0x3
            sim.setPRIV(mpp.toInt())

            /* mpp is set to U-mode (mstatus.mpp <-- 0b00) */
            sim.setSReg(SpecialRegisters.MSTATUS.address, (sim.getSReg(SpecialRegisters.MSTATUS.address) and (-1 xor (0x3 shl 11))))

            val newPC = sim.getSReg(SpecialRegisters.MEPC.address)
            sim.setPC(newPC.toInt())
        }
    }
}