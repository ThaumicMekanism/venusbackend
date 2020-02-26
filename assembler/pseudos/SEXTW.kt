package venusbackend.assembler.pseudos

/* ktlint-disable no-wildcard-imports */
import venusbackend.assembler.*
/* ktlint-enable no-wildcard-imports */
import venusbackend.assembler.AssemblerPassOne

/** Writes pseudoinstruction `sext.w rd, rs` */
object SEXTW : PseudoWriter() {
    override operator fun invoke(args: LineTokens, state: AssemblerPassOne, dbg: DebugInfo): List<LineTokens> {
        if (args[0] !== "sext.w") {
            throw AssemblerError("The format for this function is wrong!", dbg)
        }
        checkArgsLength(args, 3, dbg)
        return listOf(listOf("addiw", args[1], args[2], "0"))
    }
}