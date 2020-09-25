package venusbackend.simulator

import venusbackend.riscv.MachineCode

interface SimulatorPlugin {
    /** called on every step of the simulation (after `sim.step()`) */
    fun init(sim: Simulator)
    fun onStep(sim: Simulator, inst: MachineCode, prevPC: Number)
    fun reset(sim: Simulator)
    fun finish(sim: Simulator, any: Any? = null): Any?
}