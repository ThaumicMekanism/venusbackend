package venusbackend.simulator

import venusbackend.riscv.insts.InstructionNotFoundError

/**
 * Thrown when errors occur during simulation.
 */
class SimulatorError : Throwable {
    var infe: InstructionNotFoundError? = null
    var handled: Boolean? = null
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null, infe: InstructionNotFoundError? = null, handled: Boolean? = null) : super(msg) {
        this.infe = infe
        this.handled = handled
    }
}
