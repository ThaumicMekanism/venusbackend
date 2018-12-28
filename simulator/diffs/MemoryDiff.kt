package venusbackend.simulator.diffs

import venusbackend.simulator.Diff
import venusbackend.simulator.SimulatorState

class MemoryDiff(val addr: Number, val value: Number) : Diff {
    override operator fun invoke(state: SimulatorState) {
        state.mem.storeWord(addr, value)
    }
}