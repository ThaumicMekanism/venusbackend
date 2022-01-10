package venusbackend.assembler.pseudos

import venusbackend.assembler.AssemblerPassOne
import venusbackend.assembler.DebugInfo
import venusbackend.assembler.LineTokens
import venusbackend.assembler.PseudoWriter

/** Writes pseudoinstruction: csrs csr, rs --> csrrs x0, csr, rs */
object CSRS : PseudoWriter() {
    override operator fun invoke(args: LineTokens, state: AssemblerPassOne, dbg: DebugInfo): List<LineTokens> {
        checkArgsLength(args, 3, dbg)
        return listOf(listOf("csrrs", "x0", args[1], args[2]))
    }
}
