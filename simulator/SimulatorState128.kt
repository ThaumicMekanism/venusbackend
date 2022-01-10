package venusbackend.simulator

import venusbackend.numbers.QuadWord
import venusbackend.numbers.toQuadWord
import venusbackend.riscv.MemorySegments
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.cache.CacheHandler

class SimulatorState128 : SimulatorState {
    /*Register 32 is the special register.*/
    private val regs128 = Array(33) { QuadWord() }
    private val fregs = Array(33) { Decimal() }
    private var pc: QuadWord = QuadWord()
    private var maxpc: QuadWord = MemorySegments.TEXT_BEGIN.toQuadWord()
    private var heapEnd = MemorySegments.HEAP_BEGIN.toQuadWord()

    override val registerWidth = 128
    override var mem = Memory()
    override var cache = CacheHandler(1)
    override fun setCacheHandler(ch: CacheHandler) {
        cache = ch
    }
    override fun setPC(location: Number) {
        this.pc = location.toQuadWord()
    }
    override fun getPC(): Number {
        return this.pc
    }
    override fun incPC(amount: Number) {
        this.pc += amount.toQuadWord()
    }
    override fun setMaxPC(location: Number) {
        this.maxpc = location.toQuadWord()
    }
    override fun getMaxPC(): Number {
        return this.maxpc
    }
    override fun incMaxPC(amount: Number) {
        this.maxpc = (this.maxpc + amount.toQuadWord()).toQuadWord()
    }
    override fun getReg(i: Int) = regs128[i]
    override fun setReg(i: Int, v: Number) { if (i != 0) regs128[i] = v.toQuadWord() }
    override fun getFReg(i: Int) = fregs[i]
    override fun setFReg(i: Int, v: Decimal) { fregs[i] = v }
    override fun getSReg(i: Int): Number {
        return 0
        // TODO("Not yet implemented")
    }

    override fun setSReg(i: Int, v: Number) {
        // TODO("Not yet implemented")
    }

    override fun getHeapEnd(): Number {
        return heapEnd
    }

    override fun setHeapEnd(i: Number) {
        heapEnd = i.toQuadWord()
    }

    override fun incHeapEnd(amount: Number) {
        heapEnd += amount.toQuadWord()
    }

    override fun reset() {
        this.cache.reset()
    }
}
