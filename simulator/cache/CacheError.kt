package venusbackend.simulator.cache

import venusbackend.simulator.SimulatorError

/**
 * Thrown when errors occur during cHandler op.
 */
class CacheError : SimulatorError {
    /**
     * @param msg the message to error with
     */
    constructor(msg: String? = null) : super(msg)
}
