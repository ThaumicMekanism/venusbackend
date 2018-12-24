package venusbackend.simulator.diffs

import venusbackend.riscv.Address
import venusbackend.simulator.Diff
import venusbackend.simulator.SimulatorState

class CacheDiff(val addr: Address) : Diff {
    override operator fun invoke(state: SimulatorState) {
        state.cache.undoAccess(addr)
    }
}