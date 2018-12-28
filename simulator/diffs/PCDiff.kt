package venusbackend.simulator.diffs

import venusbackend.simulator.Diff
import venusbackend.simulator.SimulatorState

class PCDiff(val pc: Number) : Diff {
    override operator fun invoke(state: SimulatorState) { state.setPC(pc) }
}