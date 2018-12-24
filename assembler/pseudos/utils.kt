package venusbackend.assembler.pseudos

import venusbackend.assembler.AssemblerError
import venusbackend.riscv.Settings

fun checkArgsLength(args: List<String>, required: Int) {
    if (args.size != required) throw AssemblerError("wrong # of arguments")
}

fun checkStrictMode() {
    if (Settings.strict) throw AssemblerError("can't use this instruction in strict mode")
}
