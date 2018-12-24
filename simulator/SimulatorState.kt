package venusbackend.simulator

import venusbackend.riscv.MemorySegments
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.cache.CacheHandler

class SimulatorState {
    /*Register 33 is the special register.*/
    private val regs = IntArray(33)
    private val fregs = Array(33, { i: Int -> Decimal() })
    val mem = Memory()
    var cache = CacheHandler(1)
    var pc: Int = 0
    var heapEnd: Int = MemorySegments.HEAP_BEGIN
    fun getReg(i: Int) = regs[i]
    fun setReg(i: Int, v: Int) { if (i != 0) regs[i] = v }
    fun getFReg(i: Int) = fregs[i]
    fun setFReg(i: Int, v: Decimal) { fregs[i] = v }
}
