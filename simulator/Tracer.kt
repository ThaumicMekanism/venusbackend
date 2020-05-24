package venusbackend.simulator

import venusbackend.compareTo
import venusbackend.minus
import venusbackend.plus
import venusbackend.riscv.MachineCode
import venusbackend.riscv.MemorySegments

/**
 * Created by Thaumic on 7/14/2018.
 */
class Tracer(var sim: Simulator) {
    var version = "v2.0.1"
    var format = "%output%%0%\t%1%\t%2%\t%3%\t%4%\t%5%\t%6%\t%7%\t%8%\t%9%\t%10%\t%11%\t%12%\t%13%\t%14%\t%15%\t%16%\t%17%\t%18%\t%19%\t%20%\t%21%\t%22%\t%23%\t%24%\t%25%\t%26%\t%27%\t%28%\t%29%\t%30%\t%31%\t%line%\t%pc%\t%inst%\n"
    var amtReg = 32
    var base = 2
    var totCommands = -1
    var instFirst = false
    private var prevInst = MachineCode(0)
    var maxSteps = 1000000
    var tr = TraceEncapsulation()
    var twoStage = false

    companion object {
        var wordAddressed = false
    }

    fun trace() {
        traceStart()
        var err: SimulatorError? = null
        while (!sim.isDone()) {
            try {
                traceStep()
            } catch (e: SimulatorError) {
                err = e
                break
            }
        }
        traceEnd()
        if (err != null) {
            this.traceAddError(err)
        }
    }

    fun traceAddError(err: SimulatorError) {
        this.tr.trace.add(Trace(false, false, "", Array(0) { 0 }, MachineCode(0), 0, 0, error = err))
    }

    fun traceFullReset() {
        this.tr.traced = false
        this.tr.trace = ArrayList()
        this.tr.prevTrace = null
        this.tr.traceLine = 0
        this.tr.str = ""
        this.tr.stred = false
        this.tr.stringIndex = 0
    }

    fun traceStart() {
        traceFullReset()
        sim.reset(keep_args = true)
        if (this.twoStage) {
            this.tr.trace.add(Trace(didBrach(), didJump(), getecallMsg(), getRegs(), if (!sim.isDone()) sim.getNextInstruction() else MachineCode(0), this.tr.traceLine, sim.getPC()))
            this.tr.traceLine++
        }
        if (!this.instFirst && !sim.isDone()) {
            prevInst = sim.getNextInstruction()
            sim.step()
        }
    }

    fun traceStep() {
        if (sim.isDone()) {
            return
        }
        if (this.tr.traceLine > this.maxSteps && this.maxSteps > 0) {
            throw SimulatorError("The max number of steps (" + this.maxSteps + ") in the tracer has been reached! You can increase this in the settings or disable it by setting it to 0 or less. This is the current safty for infinitely looping programs.")
        }
        val currentTrace = getSingleTrace(this.tr.traceLine)
        currentTrace.prevTrace = this.tr.prevTrace
        this.tr.trace.add(currentTrace)
        this.tr.prevTrace = currentTrace
        sim.step()
        this.tr.traceLine++
    }

    fun traceEnd() {
        val currentTrace = getSingleTrace(this.tr.traceLine)
        currentTrace.prevTrace = this.tr.prevTrace
        this.tr.trace.add(currentTrace)
        this.tr.traced = true
        sim.reset(keep_args = true)
        if (this.twoStage) {
            var i = this.tr.trace.lastIndex
            if (i < 0) {
                return
            }
            var prevmc = MachineCode(0)
            var prevpc = this.tr.trace[i].pc
//            if (!this.tr.trace[i].branched and !this.tr.trace[i].jumped) {
                prevpc += prevmc.length
//            }
            while (i > 0) {
                /*FIXME Make this loop another call.*/
                val cur = this.tr.trace[i]
                val curmc = cur.inst
                val curpc = cur.pc
                cur.inst = prevmc
                cur.pc = prevpc
                prevmc = curmc
                prevpc = curpc
                i--
            }
        }
    }

    fun getSingleTrace(line: Int): Trace {
        var mc = MachineCode(0)
        if (!sim.isDone()) {
            mc = sim.getNextInstruction()
        }
        if (!this.instFirst) {
            val t = mc
            mc = prevInst
            prevInst = t
        }
        return Trace(didBrach(), didJump(), getecallMsg(), getRegs(), mc, line, sim.getPC())
    }

    fun getRegs(): Array<Number> {
        val r = Array<Number>(amtReg) { 0 }
        for (i in 0..(amtReg - 1)) {
            r[i] = sim.getReg(i)
        }
        return r
    }

    fun didBrach(): Boolean {
        return sim.branched
    }
    fun didJump(): Boolean {
        return sim.jumped
    }

    fun getecallMsg(): String {
        return sim.ecallMsg
    }

    fun getString(): String {
        if (!this.tr.stred) {
            throw SimulatorError("The trace string has not finished!")
        }
        return this.tr.str
    }

    fun traceStringStart() {
        if (!this.tr.traced) {
            throw SimulatorError("You need to make the run the trace before you can get the trace string!")
        }
        val tr = this.tr.trace
        cleanFormat()
        this.tr.reset()
    }

    fun traceStringStep(): Boolean {
        val t = this.tr.getNextTrace()
        if (twoStage && this.instFirst) {
            if (this.tr.peak().branched) {
                traceStringBranchHelper(t)
            }
            if (t.branched && this.instFirst && this.twoStage) {
//                t.pc += 4
            }
            if (this.tr.peak().jumped) {
                if (!this.twoStage) {
                    t.regs = this.tr.peak().regs.copyOf()
                }
                t.prevTrace?.regs = t.regs.copyOf()
                traceStringJumpHelper(t)
                if (this.twoStage) {
                    t.regs = this.tr.peak().regs.copyOf()
                }
            }
        }
        t.line = this.tr.stringIndex
        val peaked = this.tr.peak()
        if (twoStage && this.instFirst && (peaked.jumped || peaked.branched) && t.pc > sim.getMaxPC() && t == peaked) {
            t.pc = t.pc.toInt() + 4
        }

        this.tr.str += t.getString(format, base)
        this.tr.stringIndex++
        if (twoStage && !this.instFirst) {
            if (t.branched) {
                traceStringBranchHelper(t)
            }
            if (t.jumped) {
                /*FIXME There need to make it work w/o inst first and two stage.*/
                t.regs = this.tr.peak().regs.copyOf()
                t.prevTrace?.regs = t.regs.copyOf()
                traceStringJumpHelper(t)
            }
        }
        if (this.totCommands > 0 && this.tr.stringIndex >= this.totCommands) {
            return false
        }
        return this.tr.hasNext()
    }

    fun traceStringBranchHelper(t: Trace) {
        val pt = t.prevTrace
        val flushed = pt?.copy() ?: this.getSingleTrace(-1)
        var nextPC = flushed.pc + flushed.inst.length
        flushed.pc = nextPC
        nextPC -= MemorySegments.TEXT_BEGIN
        flushed.inst = if (nextPC < this.sim.getMaxPC()) this.sim.getInstAt(nextPC) else MachineCode(0)
        flushed.line = this.tr.stringIndex
        if (this.instFirst && this.twoStage) {
            flushed.regs = t.regs
        }
        this.tr.str += flushed.getString(format, base)
        this.tr.stringIndex++
    }
    fun traceStringJumpHelper(t: Trace) {
        val pt = t.prevTrace
        val flushed = pt?.copy() ?: this.getSingleTrace(-1)
        var nextPC = flushed.pc + flushed.inst.length
        flushed.pc = nextPC
        nextPC -= MemorySegments.TEXT_BEGIN
        flushed.inst = if (nextPC < this.sim.getMaxPC()) this.sim.getInstAt(nextPC) else MachineCode(0)
        flushed.line = this.tr.stringIndex
        if (this.instFirst && this.twoStage) {
//            flushed.regs = t.regs
        }
        this.tr.str += flushed.getString(format, base)
        this.tr.stringIndex++
    }

    fun traceStringEnd() {
        val tr = this.tr.trace
        val t = tr[tr.size - 1]
        while (this.tr.stringIndex < this.totCommands) {
            t.inst = MachineCode(0)
            t.line++
            t.pc += t.inst.length
            this.tr.str += t.getString(format, base)
            this.tr.stringIndex++
        }
        try {
            if (this.twoStage && this.instFirst && this.tr.trace[2].jumped) {
            }
        } catch (e: Throwable) {
            println("Internal error in traceString")
            println(e)
        }
        this.tr.stred = true
    }

    fun traceString() {
        traceStringStart()
        while (tr.hasNext()) {
            traceStringStep()
        }
        traceStringEnd()
    }

    private fun incPC(pc: Int): Int {
        return pc + 4
    }

    private fun cleanFormat() {
        this.format = this.format.replace("\\t", "\t").replace("\\n", "\n")
    }

    fun setWordAddressed(b: Boolean) {
        wordAddressed = b
    }
}
class TraceEncapsulation {

    lateinit var trace: ArrayList<Trace>
    var traced = false
    var prevTrace: Trace? = null
    var traceLine = 0
    var str: String = ""
    var stred = false
    var stringIndex = 0

    private var curLoc = 0
    fun getNextTrace(): Trace {
        if (!hasNext()) {
            throw IndexOutOfBoundsException("There are no more items to iterate over!")
        }
        val t = trace[curLoc]
        this.curLoc++
        return t
    }
    fun peak(): Trace {
        return trace[minOf(curLoc, trace.lastIndex)]
    }
    fun reset() {
        curLoc = 0
    }
    fun hasNext(): Boolean {
        return curLoc < trace.size
    }
    fun numberLeft(): Int {
        return trace.size - curLoc
    }
}
