package venusbackend.simulator

/**
 * Thrown when errors occur when memory is not aligned.
 */
class AlignmentError : Throwable {
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null) : super(msg)
}
