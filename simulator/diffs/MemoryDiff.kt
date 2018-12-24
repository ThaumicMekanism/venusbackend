package venusbackend.simulator.diffs

import venusbackend.simulator.Diff
import venusbackend.simulator.SimulatorState

class MemoryDiff(val addr: Int, val value: Int) : Diff {
    override operator fun invoke(state: SimulatorState) {
        state.mem.storeWord(addr, value)
    }
}