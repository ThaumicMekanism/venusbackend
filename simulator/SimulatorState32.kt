package venusbackend.simulator

import venusbackend.riscv.MemorySegments
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.cache.CacheHandler

class SimulatorState32 : SimulatorState {
    /* Register 32 is the special register. */
    private val regs32 = Array(33) { 0 }
    private val fregs = Array(33) { Decimal() }
    private var pc: Int = 0
    private var maxpc: Int = MemorySegments.TEXT_BEGIN
    private var heapEnd = MemorySegments.HEAP_BEGIN

    override val registerWidth = 32
    override var mem = Memory()
    override var cache = CacheHandler(1)
    override fun setCache(ch: CacheHandler) {
        cache = ch
    }
    override fun setPC(location: Number) {
        this.pc = location.toInt()
    }
    override fun getPC(): Number {
        return this.pc
    }
    override fun incPC(amount: Number) {
        this.pc += amount.toInt()
    }
    override fun setMaxPC(location: Number) {
        this.maxpc = location.toInt()
    }
    override fun getMaxPC(): Number {
        return this.maxpc
    }
    override fun incMaxPC(amount: Number) {
        this.maxpc = (this.maxpc + amount.toInt())
    }
    override fun getReg(i: Int) = regs32[i]
    override fun setReg(i: Int, v: Number) { if (i != 0) regs32[i] = v.toInt() }
    override fun getFReg(i: Int) = fregs[i]
    override fun setFReg(i: Int, v: Decimal) { fregs[i] = v }
    override fun getHeapEnd(): Number {
        return heapEnd
    }

    override fun setHeapEnd(i: Number) {
        heapEnd = i.toInt()
    }

    override fun incHeapEnd(amount: Number) {
        heapEnd += amount.toInt()
    }

    override fun reset() {
        this.cache.reset()
    }
}
