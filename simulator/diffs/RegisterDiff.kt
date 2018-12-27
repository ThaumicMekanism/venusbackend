package venusbackend.simulator.diffs

import venusbackend.simulator.Diff
import venusbackend.simulator.SimulatorState
import java.math.BigInteger

class RegisterDiff16(val id: Int, val v: Short) : Diff {
    override operator fun invoke(state: SimulatorState) = state.setReg(id, v)
}

class RegisterDiff(val id: Int, val v: Int) : Diff {
    override operator fun invoke(state: SimulatorState) = state.setReg(id, v)
}

class RegisterDiff64(val id: Int, val v: Long) : Diff {
    override operator fun invoke(state: SimulatorState) = state.setReg(id, v)
}

class RegisterDiffAny(val id: Int, val v: BigInteger) : Diff {
    override operator fun invoke(state: SimulatorState) = state.setReg(id, v)
}