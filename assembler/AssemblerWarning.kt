package venusbackend.assembler

/**
 * Thrown when errors occur during assembly.
 *
 */
class AssemblerWarning : Throwable {
    var line: Int? = null

    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null) : super(msg)

    /**
     * @param errorLine the line the error occurred on
     * @param e the original error to pass along
     */
    constructor(errorLine: Int, e: Throwable) : this(e.message) {
        line = errorLine
    }

    override fun toString(): String {
        if (line == null) return super.toString()
        else return "${super.toString()} on line $line"
    }
}
