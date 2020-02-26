package venusbackend.assembler.pseudos

/* ktlint-disable no-wildcard-imports */
import venusbackend.assembler.*
/* ktlint-enable no-wildcard-imports */
import venusbackend.assembler.AssemblerPassOne

/** Writes pseudoinstruction `fmv.d rd, rs` */
object FMVD : PseudoWriter() {
    override operator fun invoke(args: LineTokens, state: AssemblerPassOne, dbg: DebugInfo): List<LineTokens> {
        if (args[0] !== "fmv.d") {
            throw AssemblerError("The format for this function is wrong!", dbg)
        }
        checkArgsLength(args, 3, dbg)
        return listOf(listOf("fsgnj.d", args[1], args[2], args[2]))
    }
}