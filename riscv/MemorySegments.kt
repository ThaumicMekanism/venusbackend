package venusbackend.riscv

import venusbackend.simulator.SimulatorError

/** A singleton containing constants which say where various segments start */
object MemorySegments {
    /** Memory address where the stack segment starts (growing downwards) */
    const val STACK_BEGIN = 0x7fff_fff0
    /** Memory address where the heap segment starts */
    const val HEAP_BEGIN = 0x1000_8000
    /** Memory address where the data segment starts */
    var STATIC_BEGIN = 0x1000_0000
    /**
     * Memory address where the text segment starts
     */
    public var TEXT_BEGIN = 0x0000_0000

    fun setTextBegin(i: Int) {
        if (i < 0) {
            throw SimulatorError("The text location must be a positive number!")
        } else if (i >= MemorySegments.STATIC_BEGIN) {
            /*
            * @todo add check to see if text plus program is above stack. Should error as well.
            */
            throw SimulatorError("The text location in memory cannot be larger than the static!")
        }
        MemorySegments.TEXT_BEGIN = i
    }
}
