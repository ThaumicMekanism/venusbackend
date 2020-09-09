package venusbackend.simulator

import venusbackend.riscv.MachineCode

interface SimulatorPlugin {
    /** called on every step of the simulation (after `sim.step()`) */
    fun onStep(inst: MachineCode, prevPC: Number)
}