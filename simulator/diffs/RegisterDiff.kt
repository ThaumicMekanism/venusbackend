package venusbackend.simulator.diffs

import venusbackend.simulator.Diff
import venusbackend.simulator.SimulatorState

class RegisterDiff(val id: Int, val v: Int) : Diff {
    override operator fun invoke(state: SimulatorState) = state.setReg(id, v)
}