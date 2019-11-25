package venusbackend.assembler.pseudos

import venusbackend.assembler.AssemblerPassOne
import venusbackend.assembler.LineTokens
import venusbackend.assembler.PseudoWriter
import venusbackend.riscv.insts.dsl.relocators.ImmAbsStoreRelocator
import venusbackend.riscv.insts.dsl.relocators.PCRelHiRelocator
import venusbackend.riscv.insts.dsl.relocators.PCRelLoStoreRelocator
import venusbackend.riscv.userStringToInt

/**
 * Writes a store pseudoinstruction. (Those applied to a label)
 */
object Store : PseudoWriter() {
    override operator fun invoke(args: LineTokens, state: AssemblerPassOne): List<LineTokens> {
        checkArgsLength(args, 4)
        val hasParens = args[3].startsWith('(')
        val label = args[2]
        val arg3 =
                if (hasParens) args[3].substring(1, args[3].length - 1) else args[3]
        try {
            userStringToInt(label)
            /* if it's a number, this is just an ordinary store instruction */
            return listOf(listOf(args[0], args[1], label, arg3))
        } catch (e: NumberFormatException) {
            if (hasParens) {
                state.addRelocation(ImmAbsStoreRelocator, state.getOffset(),
                        label)
                return listOf(listOf(args[0], args[1], "0", arg3))
            }
            /* assume it's a label */
        }
        val auipc = listOf("auipc", arg3, "0")
        state.addRelocation(PCRelHiRelocator, state.getOffset(), label)
        val store = listOf(args[0], args[1], "0", arg3)
        state.addRelocation(PCRelLoStoreRelocator, state.getOffset() + 4, label)
        return listOf(auipc, store)
    }
}
