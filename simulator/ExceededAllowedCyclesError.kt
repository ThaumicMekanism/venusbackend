package venusbackend.simulator

import venusbackend.riscv.insts.InstructionNotFoundError

/**
 * Thrown when errors occur during simulation.
 */
class ExceededAllowedCyclesError : SimulatorError {
    override var infe: InstructionNotFoundError? = null
    override var handled: Boolean? = null
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null, infe: InstructionNotFoundError? = null, handled: Boolean? = null) : super(msg) {
        this.infe = infe
        this.handled = handled
    }
}
