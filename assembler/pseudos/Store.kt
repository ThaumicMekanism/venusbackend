package venusbackend.assembler.pseudos

import venusbackend.assembler.AssemblerPassOne
import venusbackend.assembler.LineTokens
import venusbackend.assembler.PseudoWriter
import venusbackend.riscv.insts.dsl.relocators.PCRelHiRelocator
import venusbackend.riscv.insts.dsl.relocators.PCRelLoStoreRelocator
import venusbackend.riscv.userStringToInt

/**
 * Writes a store pseudoinstruction. (Those applied to a label)
 */
object Store : PseudoWriter() {
    override operator fun invoke(args: LineTokens, state: AssemblerPassOne): List<LineTokens> {
        checkArgsLength(args, 4)
        val label = args[2]
        try {
            userStringToInt(label)
            /* if it's a number, this is just an ordinary store instruction */
            return listOf(args)
        } catch (e: NumberFormatException) {
            /* assume it's a label */
        }

        val auipc = listOf("auipc", args[3], "0")
        state.addRelocation(PCRelHiRelocator, state.getOffset(), label)

        val store = listOf(args[0], args[1], "0", args[3])
        state.addRelocation(PCRelLoStoreRelocator, state.getOffset() + 4, label)

        return listOf(auipc, store)
    }
}
