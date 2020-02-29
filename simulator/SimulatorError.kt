package venusbackend.simulator

import venusbackend.riscv.insts.InstructionNotFoundError

/**
 * Thrown when errors occur during simulation.
 */
open class SimulatorError : Throwable {
    open var infe: InstructionNotFoundError? = null
    open var handled: Boolean? = null
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null, infe: InstructionNotFoundError? = null, handled: Boolean? = null) : super(msg) {
        this.infe = infe
        this.handled = handled
    }
}
