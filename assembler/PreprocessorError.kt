package venusbackend.assembler

class PreprocessorError : AssemblerError {
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null, dbg: DebugInfo? = null) : super(msg, dbg)
}