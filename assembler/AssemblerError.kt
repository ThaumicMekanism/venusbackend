package venusbackend.assembler

/**
 * Thrown when errors occur during assembly.
 *
 * @todo split this into AssemblerUserError and AssemblerError
 */
open class AssemblerError : Throwable {
    companion object {
        fun parse_msg(msg: String? = null, dbg: DebugInfo? = null): String? {
            val s = msg ?: return msg
            val dbg = dbg ?: return msg
            return "${dbg.prog.name}:${dbg.lineNo}: $msg\n${dbg.line.trim()}"
        }
    }
    var line: Int? = null
    var errorType: Throwable? = null
    var dbg: DebugInfo? = null

    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null, dbg: DebugInfo? = null) : super(parse_msg(msg, dbg)) {
        this.dbg = dbg
    }

    /**
     * @param errorLine the line the error occurred on
     * @param e the original error to pass along
     */
    constructor(errorLine: Int, e: Throwable) : this(e.message) {
        line = errorLine
    }

    constructor(msg: String? = null, errorType: Throwable, dbg: DebugInfo? = null) : this(msg, dbg) {
        this.errorType = errorType
        this.dbg = dbg
    }

    override fun toString(): String {
        return super.toString()
//        if (line == null) return super.toString()
//        else return "${super.toString()} on line $line"
    }
}
