package venusbackend.riscv.insts.dsl.impls

import venusbackend.riscv.MachineCode
import venusbackend.simulator.Simulator

class RawImplementation(private val eval: (MachineCode, Simulator) -> Unit) : InstructionImplementation {
    override operator fun invoke(mcode: MachineCode, sim: Simulator) = eval(mcode, sim)
}
