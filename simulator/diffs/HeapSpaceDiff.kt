package venusbackend.simulator.diffs

import venusbackend.simulator.Diff
import venusbackend.simulator.SimulatorState

class HeapSpaceDiff(val heapEnd: Int) : Diff {
    override operator fun invoke(state: SimulatorState) { state.heapEnd = heapEnd }
}