package venusbackend.simulator

import venusbackend.riscv.insts.InstructionNotFoundError

/**
 * Thrown when errors occur during simulation.
 */
class SimulatorError : Throwable {
    var infe: InstructionNotFoundError? = null
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null, infe: InstructionNotFoundError? = null) : super(msg) {
        this.infe = infe
    }
}
