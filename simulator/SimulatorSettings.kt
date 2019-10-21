package venusbackend.simulator

/**
 * Created by thaum on 7/31/2018.
 */
data class SimulatorSettings(
    var alignedAddress: Boolean = false,
    var mutableText: Boolean = true,
    var ecallOnlyExit: Boolean = false,
    var setRegesOnInit: Boolean = true,
    var maxSteps: Int = -1,
    var ASLR: Boolean = false,
    var NX_bit: Boolean = false,
    var allowAccessBtnStackHeap: Boolean = false
)