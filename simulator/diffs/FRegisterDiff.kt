package venusbackend.simulator.diffs

import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.Diff
import venusbackend.simulator.SimulatorState

class FRegisterDiff(val id: Int, val v: Decimal) : Diff {
    override operator fun invoke(state: SimulatorState) = state.setFReg(id, v)
}