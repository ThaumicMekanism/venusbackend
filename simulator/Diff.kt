package venusbackend.simulator

interface Diff {
    operator fun invoke(state: SimulatorState)
}