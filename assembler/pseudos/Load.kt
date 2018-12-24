package venusbackend.assembler.pseudos

import venusbackend.assembler.AssemblerPassOne
import venusbackend.assembler.LineTokens
import venusbackend.assembler.PseudoWriter
import venusbackend.riscv.insts.dsl.relocators.PCRelHiRelocator
import venusbackend.riscv.insts.dsl.relocators.PCRelLoRelocator

/**
 * Writes a load pseudoinstruction. (Those applied to a label)
 */
object Load : PseudoWriter() {
    override operator fun invoke(args: LineTokens, state: AssemblerPassOne): List<LineTokens> {
        checkArgsLength(args, 3)

        val auipc = listOf("auipc", args[1], "0")
        state.addRelocation(PCRelHiRelocator, state.getOffset(), args[2])

        val load = listOf(args[0], args[1], "0", args[1])
        state.addRelocation(PCRelLoRelocator, state.getOffset() + 4, args[2])

        return listOf(auipc, load)
    }
}
