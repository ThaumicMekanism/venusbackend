package venusbackend.simulator.cache

/**
 * Thrown when errors occur during cHandler op.
 */
class CacheError : Throwable {
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null) : super(msg)
}
