package venusbackend.simulator

import venusbackend.riscv.MemorySegments
import venusbackend.riscv.insts.floating.Decimal
import venusbackend.simulator.cache.CacheHandler

class SimulatorState64 : SimulatorState {
    /*Register 32 is the special register.*/
    private val regs64 = Array(33) { 0.toLong() }
    private val fregs = Array(33) { Decimal() }
    private var pc: Long = 0
    private var maxpc: Long = MemorySegments.TEXT_BEGIN.toLong()
    private var heapEnd = MemorySegments.HEAP_BEGIN.toLong()    

    override val registerWidth = 64
    override var mem = Memory()
    override var cache = CacheHandler(1)

    private val sregs64 = mutableMapOf<Int, CSR64>()
    private var priv: Int = 3 /* RISC-V privilege level the hart is running with [0 = user/application = U; 1 = Supervisor = S; 2 = Reserved; 3 = Machine = M ] */    
    init {
        /**
         * Add CSRs here. Take the number from the "Currently allocated RISC-V x-level CSR addresses" table
         */
        sregs64[SpecialRegisters.MSTATUS.address] = CSR64(0, SpecialRegisterRights.MRW) // mstatus CSR
        sregs64[SpecialRegisters.MIE.address] = CSR64(0, SpecialRegisterRights.MRW) // mie CSR
        sregs64[SpecialRegisters.MIP.address] = CSR64(0, SpecialRegisterRights.MRW) // mip CSR
        sregs64[SpecialRegisters.MEPC.address] = CSR64(0, SpecialRegisterRights.MRW) // mepc CSR
        sregs64[SpecialRegisters.MCAUSE.address] = CSR64(0, SpecialRegisterRights.MRW) // mcause CSR
        sregs64[SpecialRegisters.MTVEC.address] = CSR64(0, SpecialRegisterRights.MRW) // mtvec CSR
        sregs64[SpecialRegisters.MTIME.address] = CSR64(0, SpecialRegisterRights.MRW)
        sregs64[SpecialRegisters.MTIMECMP.address] = CSR64(0, SpecialRegisterRights.MRW)
        sregs64[SpecialRegisters.MSCRATCH.address] = CSR64(0, SpecialRegisterRights.MRW)
        sregs64[SpecialRegisters.MTVAL.address] = CSR64(0, SpecialRegisterRights.MRW)

        this.priv = 3; /* start in M-mode */ 
    }

    override fun setCacheHandler(ch: CacheHandler) {
        cache = ch
    }
    override fun setPC(location: Number) {
        this.pc = location.toLong()
    }
    override fun getPC(): Number {
        return this.pc
    }
    override fun incPC(amount: Number) {
        this.pc += amount.toLong()
    }
    override fun setMaxPC(location: Number) {
        this.maxpc = location.toLong()
    }
    override fun getMaxPC(): Number {
        return this.maxpc
    }
    override fun incMaxPC(amount: Number) {
        this.maxpc = (this.maxpc + amount.toLong())
    }
    override fun getReg(i: Int) = regs64[i]
    override fun setReg(i: Int, v: Number) { if (i != 0) regs64[i] = v.toLong() }
    override fun getFReg(i: Int) = fregs[i]
    override fun setFReg(i: Int, v: Decimal) { fregs[i] = v }
    
    override fun getSReg(i: Int): Number {
        val result: Long
        result = sregs64[i]!!.content
        return result
    }
    override fun setSReg(i: Int, v: Number) {
        if (sregs64[i]!!.specialRegisterRights == SpecialRegisterRights.MRW) { // Checking just machine Read/Write privilege because we only have machine mode
            sregs64[i]!!.content = v.toLong()
        }
    }

    override fun setPRIV(newPriv: Int) {
        this.priv = newPriv and 0x3 // modulo 3
    }
    override fun getPRIV(): Int {
        return this.priv
    }

    override fun getHeapEnd(): Number {
        return heapEnd
    }

    override fun setHeapEnd(i: Number) {
        heapEnd = i.toLong()
    }

    override fun incHeapEnd(amount: Number) {
        heapEnd += amount.toLong()
    }

    override fun reset() {
        this.cache.reset()

        this.priv = 3; /* start in M-mode */
    }
    private data class CSR64(var content: Long, val specialRegisterRights: SpecialRegisterRights)
}
