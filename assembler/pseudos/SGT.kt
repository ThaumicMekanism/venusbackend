package venusbackend.assembler.pseudos

import venusbackend.assembler.AssemblerPassOne
import venusbackend.assembler.LineTokens
import venusbackend.assembler.PseudoWriter

/**
 * Writes pseudoinstruction `sgt` (set greater than)
 * @todo add a settings option for "extended pseudoinstructions"
 */
object SGT : PseudoWriter() {
    override operator fun invoke(args: LineTokens, state: AssemblerPassOne): List<LineTokens> {
        checkArgsLength(args, 4)
        checkStrictMode()
        val unsigned = if (args[0].endsWith("u")) "u" else ""
        return listOf(listOf("slt$unsigned", args[1], args[3], args[2]))
    }
}
