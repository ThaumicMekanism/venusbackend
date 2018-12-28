package venusbackend.simulator

import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.cache.CacheHandler

interface SimulatorState {
    var mem: Memory
    var cache: CacheHandler
    val registerWidth: Int
    fun getPC(): Number
    fun setPC(location: Number)
    fun incPC(amount: Number)
    fun getReg(i: Int): Number
    fun setReg(i: Int, v: Number)
    fun getFReg(i: Int): Decimal
    fun setFReg(i: Int, v: Decimal)
    fun getHeapEnd(): Number
    fun incHeapEnd(amount: Number)
    fun setHeapEnd(i: Number)
}
