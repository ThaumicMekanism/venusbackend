package venusbackend.simulator

import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.cache.CacheHandler

interface SimulatorState {
    var mem: Memory
    var cache: CacheHandler
    fun setCacheHandler(ch: CacheHandler)
    val registerWidth: Int
    fun getPC(): Number
    fun setPC(location: Number)
    fun incPC(amount: Number)
    fun getMaxPC(): Number
    fun setMaxPC(location: Number)
    fun incMaxPC(amount: Number)
    fun getReg(i: Int): Number
    fun setReg(i: Int, v: Number)
    fun getFReg(i: Int): Decimal
    fun setFReg(i: Int, v: Decimal)
    fun getSReg(i: Int): Number
    fun setSReg(i: Int, v: Number)
    fun getHeapEnd(): Number
    fun incHeapEnd(amount: Number)
    fun setHeapEnd(i: Number)
    fun reset()
}

enum class SpecialRegisterRights {
    URW, URO, SRW, MRO, MRW, DRW
}

enum class SpecialRegisters(val address: Int, val regName: String) {
    MSTATUS(0x300, "mstatus"),
    MIE(0x304, "mie"),
    MTVEC(0x305, "mtvec"),
    MSCRATCH(0x340, "mscratch"),
    MEPC(0x341, "mepc"),
    MCAUSE(0x342, "mcause"),
    MTVAL(0x343, "mtval"),
    MIP(0x344, "mip"),
    MTIME(0x701, "mtime"),
    MTIMECMP(0x321, "mtimecmp")
}
