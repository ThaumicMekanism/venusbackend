package venusbackend.assembler.pseudos

import venusbackend.assembler.AssemblerError
import venusbackend.assembler.DebugInfo
import venusbackend.riscv.Settings

fun checkArgsLength(args: List<String>, required: Int, dbg: DebugInfo) {
    if (args.size != required) throw AssemblerError("wrong # of arguments", dbg)
}

fun checkStrictMode() {
    if (Settings.strict) throw AssemblerError("can't use this instruction in strict mode")
}
