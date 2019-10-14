package venusbackend.riscv.insts

/**
 * Created by thaum on 8/6/2018.
 */
/**
 * Thrown when errors occur when an instruction is not supported by the instruction set.
 */
open class InstructionNotSupportedError : Throwable {
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null) : super(msg)
}
