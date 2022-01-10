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
    private val sregs32 = mutableMapOf<Int, CSR32>()
    init {
        /**
         * Add CSRs here. Take the number from the "Currently allocated RISC-V x-level CSR addresses" table
         */
         /*
            spcified in: SimulatorState.kt
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
          */
          /*
            TODO: make more robust (e.g. loop over enum)!
           */
        sregs32[SpecialRegisters.MSTATUS.address] = CSR32(0, SpecialRegisterRights.MRW) // mstatus CSR
        sregs32[SpecialRegisters.MIE.address] = CSR32(0, SpecialRegisterRights.MRW) // mie CSR
        sregs32[SpecialRegisters.MIP.address] = CSR32(0, SpecialRegisterRights.MRW) // mip CSR
        sregs32[SpecialRegisters.MEPC.address] = CSR32(0, SpecialRegisterRights.MRW) // mepc CSR
        sregs32[SpecialRegisters.MCAUSE.address] = CSR32(0, SpecialRegisterRights.MRW) // mcause CSR
        sregs32[SpecialRegisters.MTVEC.address] = CSR32(0, SpecialRegisterRights.MRW) // mtvec CSR
        sregs32[SpecialRegisters.MTIME.address] = CSR32(0, SpecialRegisterRights.MRW)
        sregs32[SpecialRegisters.MTIMECMP.address] = CSR32(0, SpecialRegisterRights.MRW)
        sregs32[SpecialRegisters.MSCRATCH.address] = CSR32(0, SpecialRegisterRights.MRW)
        sregs32[SpecialRegisters.MTVAL.address] = CSR32(0, SpecialRegisterRights.MRW)    
    }

    override val registerWidth = 32
    override var mem = Memory()
    override var cache = CacheHandler(1)
    override fun setCacheHandler(ch: CacheHandler) {
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
    override fun getSReg(i: Int): Number {
        //semaphore32.acquire()
        val result : Int
        //withContext(context32) {
            result = sregs32[i]!!.content
        //}
        //semaphore32.release()
        return result
    }

    override fun setSReg(i: Int, v: Number) {
        //semaphore32.acquire()
        if (sregs32[i]!!.specialRegisterRights == SpecialRegisterRights.MRW) { // Checking just machine Read/Write privilege because we only have machine mode
            //withContext(context32) {
                sregs32[i]!!.content = v.toInt()
            //}
        }
        //semaphore32.release()
    }

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
    private data class CSR32(var content: Int, val specialRegisterRights: SpecialRegisterRights)
}
