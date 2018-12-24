package venusbackend.simulator

import venusbackend.riscv.MachineCode

/**
 * Created by Thaumic on 7/14/2018.
 */

class Trace(branched: Boolean, jumped: Boolean, ecallMsg: String, regs: IntArray, inst: MachineCode, line: Int, pc: Int) {
    var branched = false
    var jumped = false
    var ecallMsg = ""
    var regs = IntArray(0)
    var inst = MachineCode(0)
    var line = 0
    var pc = 0
    var prevTrace: Trace? = null

    init {
        this.ecallMsg = ecallMsg
        this.branched = branched
        this.jumped = jumped
        this.regs = regs
        this.inst = inst
        this.line = line
        this.pc = pc
    }

    fun getString(format: String, base: Int): String {
        if (this.ecallMsg == "exiting the venusbackend.simulator") {
            return "exiting the venusbackend.simulator\n"
        }
        var f = format.replace("%output%", this.ecallMsg).replace("%inst%", vnumToBase(base, this.inst.toString().toInt(), 32, 10, true)).replace("%pc%", vnumToBase(base, this.getPC(), 32, 10, false)).replace("%line%", vnumToBase(base, this.line, 16, 10, false))
        for (i in 0..(regs.size - 1)) {
            f = f.replace("%" + i.toString() + "%", vnumToBase(base, this.regs[i], 32, 10, true))
        }
        return f
    }

    fun getPC(): Int {
        if (wordAddressed) {
            return this.pc / 4
        }
        return this.pc
    }

    fun copy(): Trace {
        /*@fixme This is not a pure copy since modifing internal things in the copy still can affect the main.*/
        return Trace(branched, jumped, ecallMsg, regs.copyOf(), inst, line, pc)
    }
}
/*
* Takes in a base 10 integer and a base to convert it to and returns a string of what the number is.
*/
external fun vnumToBase(curNumBase: Int, n: Int, length: Int, base: Int, signextend: Boolean): String