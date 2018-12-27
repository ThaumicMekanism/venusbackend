package venusbackend.simulator

import venusbackend.riscv.MemorySegments
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.cache.CacheHandler
import java.math.BigInteger

class SimulatorState {
    /*Register 33 is the special register.*/
    private val regs16 = Array(33) { 0.toShort() }
    private val regs32 = Array(33) { 0 }
    private val regs64 = Array(33) { 0.toLong() }
    private val regsAny = Array(33) { BigInteger("0") }
    private val fregs = Array(33) { Decimal() }
    var mem = Memory()
    var cache = CacheHandler(1)
    var pc: Int = 0
    var heapEnd: Int = MemorySegments.HEAP_BEGIN
    fun getReg16(i: Int) = regs16[i]
    fun getReg32(i: Int) = regs32[i]
    fun getReg64(i: Int) = regs64[i]
    fun getRegAny(i: Int) = regsAny[i]
    fun setReg(i: Int, v: Short) { if (i != 0) regs16[i] = v }
    fun setReg(i: Int, v: Int) { if (i != 0) regs32[i] = v }
    fun setReg(i: Int, v: Long) { if (i != 0) regs64[i] = v }
    fun setReg(i: Int, v: BigInteger) { if (i != 0) regsAny[i] = v }
    fun getFReg(i: Int) = fregs[i]
    fun setFReg(i: Int, v: Decimal) { fregs[i] = v }
}
