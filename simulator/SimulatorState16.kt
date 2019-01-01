package venusbackend.simulator

import venusbackend.riscv.MemorySegments
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.cache.CacheHandler

class SimulatorState16 : SimulatorState {
    /* Register 32 is the special register. */
    private val regs16 = Array(33) { 0.toShort() }
    private val fregs = Array(33) { Decimal() }
    private var pc: Short = 0
    private var maxpc: Short = MemorySegments.TEXT_BEGIN.toShort()
    private var heapEnd = MemorySegments.HEAP_BEGIN.toShort()

    override val registerWidth = 16
    override var mem = Memory()
    override var cache = CacheHandler(1)
    override fun setPC(location: Number) {
        this.pc = location.toShort()
    }
    override fun getPC(): Number {
        return this.pc
    }
    override fun incPC(amount: Number) {
        this.pc = (this.pc + amount.toShort()).toShort()
    }
    override fun setMaxPC(location: Number) {
        this.maxpc = location.toShort()
    }
    override fun getMaxPC(): Number {
        return this.maxpc
    }
    override fun incMaxPC(amount: Number) {
        this.maxpc = (this.maxpc + amount.toShort()).toShort()
    }
    override fun getReg(i: Int) = regs16[i]
    override fun setReg(i: Int, v: Number) { if (i != 0) regs16[i] = v.toShort() }
    override fun getFReg(i: Int) = fregs[i]
    override fun setFReg(i: Int, v: Decimal) { fregs[i] = v }
    override fun getHeapEnd(): Number {
        return heapEnd
    }

    override fun setHeapEnd(i: Number) {
        heapEnd = i.toShort()
    }

    override fun incHeapEnd(amount: Number) {
        heapEnd = (heapEnd + amount.toShort()).toShort()
    }
}
