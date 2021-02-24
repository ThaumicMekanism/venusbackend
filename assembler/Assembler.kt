package venusbackend.assembler
/* ktlint-disable no-wildcard-imports */
import venusbackend.riscv.InitInstructions
import venusbackend.assembler.pseudos.checkArgsLength
import venus.Renderer
import venusbackend.riscv.*
import venusbackend.riscv.insts.InstructionNotFoundError
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.getImmWarning
import venusbackend.riscv.insts.dsl.relocators.Relocator
import venusbackend.simulator.SimulatorError
/* ktlint-enable no-wildcard-imports */

/**
 * This singleton implements a simple two-pass assembler to transform files into programs.
 */
object Assembler {
    val defaultDefines: MutableMap<String, String> = HashMap()
    /**
     * Assembles the given code into an unlinked Program.
     *
     * @param text the code to assemble.
     * @return an unlinked program.
     * @see venus.linker.Linker
     * @see venus.simulator.Simulator
     */
    fun assemble(text: String, name: String = "main.S", abspath: String = ""): AssemblerOutput {
        InitInstructions() // This is due to how some method of compilation handle all of the code.
        var (passOneProg, talInstructions, passOneErrors, warnings) = AssemblerPassOne(text.replace("\r", ""), name, abspath).run()

        /* This will force pc to be word aligned. Removed it because I guess you could custom it.
        if (passOneProg.insts.size > 0) {
            val l = passOneProg.insts[0].length
            if (MemorySegments.TEXT_BEGIN % l != 0) {
                /*This will align the pc so we do not have invalid stuffs.*/
                MemorySegments.setTextBegin(MemorySegments.TEXT_BEGIN - (MemorySegments.TEXT_BEGIN % l))
            }
        }*/

        if (passOneErrors.isNotEmpty()) {
            return AssemblerOutput(passOneProg, passOneErrors, ArrayList<AssemblerWarning>())
        }
        var passTwoOutput = AssemblerPassTwo(passOneProg, talInstructions).run()
        if (passTwoOutput.prog.textSize + MemorySegments.TEXT_BEGIN > MemorySegments.STATIC_BEGIN) {
            try {
                MemorySegments.setTextBegin(MemorySegments.STATIC_BEGIN - passOneProg.textSize)
                Renderer.updateText()
                val pone = AssemblerPassOne(text, name, abspath).run()
                passOneProg = pone.prog
                passOneErrors = pone.errors
                talInstructions = pone.talInstructions
                if (passOneErrors.isNotEmpty()) {
                    return AssemblerOutput(passOneProg, passOneErrors, ArrayList<AssemblerWarning>())
                }
                passTwoOutput = AssemblerPassTwo(passOneProg, talInstructions).run()
            } catch (e: SimulatorError) {
                throw SimulatorError("Could not change the text size so could not fit the program because the static is too low and the text would be below zero!")
            }
        }
        if (warnings.isNotEmpty()) {
            val arr = passTwoOutput.warnings.toMutableList()
            arr.addAll(warnings)
            passTwoOutput = AssemblerOutput(passTwoOutput.prog, passTwoOutput.errors, arr)
        }
        return passTwoOutput
    }
}

data class DebugInfo(val lineNo: Int, val line: String, val address: Int, val prog: Program)
data class DebugInstruction(val debug: DebugInfo, val LineTokens: List<String>)
data class PassOneOutput(
    val prog: Program,
    val talInstructions: List<DebugInstruction>,
    val errors: List<AssemblerError>,
    val warnings: List<AssemblerWarning>
)
data class AssemblerOutput(val prog: Program, val errors: List<AssemblerError>, val warnings: List<AssemblerWarning>)

/**
 * Pass #1 of our two pass assembler.
 *
 * It parses labels, expands pseudo-instructions and follows assembler directives.
 * It populations [talInstructions], which is then used by [AssemblerPassTwo] in order to actually assemble the code.
 */
val p1warnings = ArrayList<AssemblerWarning>()
internal class AssemblerPassOne(private val text: String, name: String = "anonymous", abspath: String) {
    /** The program we are currently assembling */
    private val prog = Program(name, abspath)
    /** The text offset where the next instruction will be written */
    private var currentTextOffset = 0 // MemorySegments.TEXT_BEGIN
    /** The data offset where more data will be written */
    private var currentDataOffset = MemorySegments.STATIC_BEGIN
    /** The allows user to set custom memory segments until the assembler has used an offset. */
    private var allow_custom_memory_segments = true
    /** Whether or not we are currently in the text segment */
    private var inTextSegment = true
    /** TAL Instructions which will be added to the program */
    private val talInstructions = ArrayList<DebugInstruction>()
    /** The current line number (for user-friendly errors) */
    private var currentLineNumber = 0
    /** List of all errors encountered */
    private val errors = ArrayList<AssemblerError>()
    private val warnings = ArrayList<AssemblerWarning>()
    /** Preprocessor defines */
    private val defines = HashMap<String, String>(Assembler.defaultDefines)

    class Preprocessor() {
        val DIRECTIVE_DEFINE = "#define"
        val DIRECTIVE_UNDEF = "#undef"
        val DIRECTIVE_IF = "#if"
        val DIRECTIVE_ELIF = "#elif"
        val DIRECTIVE_ELSE = "#else"
        val DIRECTIVE_ENDIF = "#endif"
        val DIRECTIVE_IFDEF = "#ifdef"
        val DIRECTIVE_IFNDEF = "#ifndef"
        val DIRECTIVE_ERROR = "#error"
        val DIRECTIVE_IMPORT = "#import"
        val DIRECTIVE_INCLUDE = "#include"
//    val DIRECTIVE_PRAGMA = "#pragma"
//    val DIRECTIVE_LINE = "#line"
//    val DIRECTIVE_USING = "#using"
        enum class IfStatus(val status: String) {
            FINDING("FINDING"),
            PROCESSING("PROCESSING"),
            PROCESSED("PROCESSED")
        }

        val ifstack = mutableListOf<IfStatus>()
        val ifstackdbg = mutableListOf<DebugInfo>()

        fun popIfStack(dbg: DebugInfo): IfStatus {
            if (ifstack.isEmpty()) {
                throw PreprocessorError("Could not find any other #if style preprocessor directives!", dbg = dbg)
            }
            ifstackdbg.removeAt(ifstackdbg.lastIndex)
            return ifstack.removeAt(ifstack.lastIndex)
        }

        fun peekIfStack(): IfStatus {
            if (ifstack.size == 0) {
                return IfStatus.PROCESSING
            }
            return ifstack.get(ifstack.lastIndex)
        }

        fun pushIfStack(status: IfStatus, dbg: DebugInfo) {
            ifstack.add(status)
            ifstackdbg.add(dbg)
        }

        fun setTopIfStack(status: IfStatus, dbg: DebugInfo) {
            popIfStack(dbg)
            pushIfStack(status, dbg)
        }

        fun preprocess(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line.trim()

            pline = process_define(pass, pline, dbg)
            pline = process_undef(pass, pline, dbg)

            pline = process_ifdef(pass, pline, dbg)
            pline = process_ifndef(pass, pline, dbg)
            pline = process_if(pass, pline, dbg)
            pline = process_elif(pass, pline, dbg)
            pline = process_else(pass, pline, dbg)
            pline = process_endif(pass, pline, dbg)

            pline = process_error(pass, pline, dbg)

            pass.defines.forEach { (token: String, value: String) ->
                val splitline = pline.split(Regex("\\s")).toMutableList()
                val tokens = ArrayList<String>()
                var diff = false
                for (v in splitline) {
                    if (v == token) {
                        tokens.add(value)
                        diff = true
                    } else {
                        tokens.add(v)
                    }
                }
                if (diff) {
                    pline = tokens.joinToString(" ")
                }
            }
            return pline
        }

        fun finish(pass: AssemblerPassOne) {
            if (ifstack.isNotEmpty()) {
                throw PreprocessorError("Use of preprocessor directive `#if`, `#ifdef`, or `#elif` which was not finished by an `#endif`.\nIf you did not mean to use preprocessor directives, add a space between the `#` and word.\n", ifstackdbg.last())
            }
        }

        fun process_define(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line
            if (pline.startsWith(DIRECTIVE_DEFINE)) {
                pline = pline.removePrefix(DIRECTIVE_DEFINE).trim()
                val tokens = pline.split(" ").toMutableList()
                pass.defines[tokens.removeAt(0)] = tokens.joinToString(" ")
            }
            return line
        }

        fun process_undef(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line
            if (pline.startsWith(DIRECTIVE_UNDEF)) {
                pline = pline.removePrefix(DIRECTIVE_UNDEF).trim()
                val tokens = pline.split(" ")
                checkArgsLength(tokens, 1, dbg)
                pass.defines.remove(tokens[0])
            }
            return line
        }

        fun process_if(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line
            if (pline.startsWith(DIRECTIVE_IF) && !(pline.startsWith(DIRECTIVE_IFDEF) || pline.startsWith(DIRECTIVE_IFNDEF))) {
                pline = pline.removePrefix(DIRECTIVE_IF).trim()
                if (peekIfStack() != IfStatus.PROCESSING) {
                    pushIfStack(IfStatus.PROCESSED, dbg)
                } else {
                    if (eval_conditional(pline, pass, line, dbg)) {
                        pushIfStack(IfStatus.PROCESSING, dbg)
                    } else {
                        pushIfStack(IfStatus.FINDING, dbg)
                    }
                }
            }
            return line
        }

        fun process_elif(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line
            if (pline.startsWith(DIRECTIVE_ELIF)) {
                pline = pline.removePrefix(DIRECTIVE_ELIF).trim()
                if (peekIfStack() == IfStatus.FINDING) {
                    if (eval_conditional(pline, pass, line, dbg)) {
                        setTopIfStack(IfStatus.PROCESSING, dbg)
                    } else {
                        setTopIfStack(IfStatus.FINDING, dbg)
                    }
                }
            }
            return line
        }

        fun process_else(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line
            if (pline.startsWith(DIRECTIVE_ELSE)) {
                if (peekIfStack() == IfStatus.FINDING) {
                    setTopIfStack(IfStatus.PROCESSING, dbg)
                } else {
                    setTopIfStack(IfStatus.PROCESSED, dbg)
                }
            }
            return line
        }

        fun process_endif(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            if (line == DIRECTIVE_ENDIF) {
                if (ifstack.size == 0) {
                    throw AssemblerError("#endif without #if", dbg = dbg)
                }
                popIfStack(dbg)
            }
            if (ifstack.size == 0) {
                return line
            }
            val ifstatus = peekIfStack()
            if (ifstatus == IfStatus.FINDING || ifstatus == IfStatus.PROCESSED) {
                // We need to clear the line if we are in a false if/elif statement
                // OR after we finished an if block but are not to the endif.
                return ""
            }
            return line
        }

        fun process_ifdef(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line
            if (pline.startsWith(DIRECTIVE_IFDEF)) {
                pline = pline.removePrefix(DIRECTIVE_IFDEF).trim()
                val tokens = pline.split(" ")
                checkArgsLength(tokens, 1, dbg)
                if (peekIfStack() == IfStatus.PROCESSING) {
                    pushIfStack(IfStatus.PROCESSED, dbg)
                } else {
                    if (pass.defines.containsKey(tokens[0])) {
                        pushIfStack(IfStatus.PROCESSING, dbg)
                    } else {
                        pushIfStack(IfStatus.FINDING, dbg)
                    }
                }
            }
            return line
        }

        fun process_ifndef(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line
            if (pline.startsWith(DIRECTIVE_IFNDEF)) {
                pline = pline.removePrefix(DIRECTIVE_IFNDEF).trim()
                val tokens = pline.split(" ")
                checkArgsLength(tokens, 1, dbg)
                if (peekIfStack() == IfStatus.PROCESSING) {
                    pushIfStack(IfStatus.PROCESSED, dbg)
                } else {
                    if (!pass.defines.containsKey(tokens[0])) {
                        pushIfStack(IfStatus.PROCESSING, dbg)
                    } else {
                        pushIfStack(IfStatus.FINDING, dbg)
                    }
                }
            }
            return line
        }

        fun process_error(pass: AssemblerPassOne, line: String, dbg: DebugInfo): String {
            var pline = line
            if (pline.startsWith(DIRECTIVE_ERROR)) {
                pline = pline.removePrefix(DIRECTIVE_ERROR).trim()
                throw AssemblerError(pline, dbg = dbg)
            }
            return line
        }

        fun eval_conditional(condition: String, pass: AssemblerPassOne, line: String, dbg: DebugInfo): Boolean {
//            val tokens = lex_conditional(condition, dbg)
//            for (token in tokens) {
//                if (!token.final) {
//
//                }
//            }
            return false
        }

        data class LexedConditional(val token: String, val final: Boolean)

        fun lex_conditional(line: String, dbg: DebugInfo): List<LexedConditional> {
            var currentWord = StringBuilder("")
            val previousWords = ArrayList<LexedConditional>()
            var parenCount = 0
            var prevEqual: Boolean = false

            for (ch in line) {
                if (prevEqual) {
                    if (ch == '=') {
                        previousWords.add(LexedConditional("==", false))
                    } else {
                        throw PreprocessorError("Invalid number of equal signs", dbg)
                    }
                }
                when (ch) {
                    '(' -> {
                        if (parenCount == 0) {
                            previousWords.add(LexedConditional(currentWord.toString(), false))
                            currentWord = StringBuilder("")
                        }
                        parenCount++
                    }
                    ')' -> {
                        if (parenCount == 0) {
                            throw PreprocessorError("Uneven parentheses count", dbg = dbg)
                        }
                        previousWords.add(LexedConditional(currentWord.toString(), true))
                        currentWord = StringBuilder("")
                        parenCount--
                    }
                    '=' -> {
                        prevEqual = true
                        previousWords.add(LexedConditional(currentWord.toString(), false))
                        currentWord = StringBuilder("")
                    }
                    ' ' -> {
                        previousWords.add(LexedConditional(currentWord.toString(), false))
                        currentWord = StringBuilder("")
                    }
                    else -> {
                        currentWord.append(ch)
                    }
                }
            }
            return previousWords
        }
    }

    fun run(): PassOneOutput {
        doPassOne()
        return PassOneOutput(prog, talInstructions, errors, warnings)
    }

    private fun doPassOne() {
        val preprocessor = Preprocessor()
        for (line in text.lines()) {
            try {
                currentLineNumber++
                val dbg = DebugInfo(currentLineNumber, line, currentTextOffset, prog)

                val offset = getOffset()

                var pline = preprocessor.preprocess(this, line, dbg)

                val (labels, args) = Lexer.lexLine(pline, dbg)
                for (label in labels) {
                    allow_custom_memory_segments = false
                    val oldOffset = prog.addLabel(label, offset)
                    if (oldOffset != null) {
                        throw AssemblerError("label $label defined twice", dbg)
                    }
                }

                if (args.isEmpty() || args[0].isEmpty()) continue // empty line

                if (isAssemblerDirective(args[0])) {
                    parseAssemblerDirective(args[0], args.drop(1), pline, dbg)
                } else {
                    allow_custom_memory_segments = false
                    val expandedInsts = replacePseudoInstructions(args, dbg)
                    for (inst in expandedInsts) {
//                        val dbg = DebugInfo(currentLineNumber, line, currentTextOffset, prog)
                        val instsize = try {
                            Instruction[getInstruction(inst), dbg].format.length
                        } catch (e: AssemblerError) {
                            4
                        }
                        talInstructions.add(DebugInstruction(dbg, inst))
                        currentTextOffset += instsize
                    }
                }
                for (p1warning in p1warnings) {
                    p1warning.line = currentLineNumber
                }
                warnings.addAll(p1warnings)
                p1warnings.clear()
            } catch (e: AssemblerError) {
                errors.add(AssemblerError(currentLineNumber, e))
            }
        }
        try {
            preprocessor.finish(this)
        } catch (e: AssemblerError) {
            if (e.dbg == null) {
                errors.add(AssemblerError(currentLineNumber, e))
            } else {
                errors.add(AssemblerError(e.dbg!!.lineNo, e))
            }
        }
    }

    /** Gets the current offset (either text or data) depending on where we are writing */
    fun getOffset() = if (inTextSegment) currentTextOffset else currentDataOffset

    /**
     * Determines if the given token is an assembler directive
     *
     * @param cmd the token to check
     * @return true if the token is an assembler directive
     * @see parseAssemblerDirective
     */
    private fun isAssemblerDirective(cmd: String) = cmd.startsWith(".")

    /**
     * Replaces any pseudoinstructions which occur in our program.
     *
     * @param tokens a list of strings corresponding to the space delimited line
     * @return the corresponding TAL instructions (possibly unchanged)
     */
    private fun replacePseudoInstructions(tokens: LineTokens, dbg: DebugInfo): List<LineTokens> {
        try {
            val cmd = getInstruction(tokens)
            // This is meant to allow for cmds with periods since the pseudodispatcher does not allow for special chars.
            val cleanedCMD = cmd.replace(".", "")
            val pw = PseudoDispatcher.valueOf(cleanedCMD).pw
            return pw(tokens, this, dbg)
        } catch (t: Throwable) {
            /* TODO: don't use throwable here */
            /* not a pseudoinstruction, or expansion failure */
            val linetokens = parsePossibleMachineCode(tokens, dbg)
            return linetokens
        }
    }

    private fun parsePossibleMachineCode(tokens: LineTokens, dbg: DebugInfo): List<LineTokens> {
        val c = getInstruction(tokens)
        if (c in listOf("beq", "bge", "bgeu", "blt", "bltu", "bne")) {
            try {
                val loc = getOffset() + userStringToInt(tokens[3])
                prog.addLabel(venusInternalLabels + loc.toString(), loc)
//                warnings.add(AssemblerWarning("Interpreting label as immediate"))
//                return listOf(listOf(tokens[0], tokens[1], tokens[2], "L" + loc.toString()))
            } catch (e: Throwable) {
                //
            }
        } else if (c == "jal") {
            try {
                val loc = getOffset() + userStringToInt(tokens[2])
                prog.addLabel(venusInternalLabels + loc.toString(), loc)
//                warnings.add(AssemblerWarning("Interpreting label as immediate"))
//                return listOf(listOf(tokens[0], tokens[1], "L" + loc.toString()))
            } catch (e: Throwable) {
                //
            }
        } else {
            try {
                var cmd = userStringToInt(c)
                try {
                    val decoded = Instruction[MachineCode(cmd)].disasm(MachineCode(cmd))
                    val lex = Lexer.lexLine(decoded, dbg).second.toMutableList()
                    if (lex[0] == "jal") {
                        val loc = getOffset() + lex[2].toInt()
                        prog.addLabel("L$loc", loc)
                        lex[2] = "L$loc"
                    }
                    if (lex[0] in listOf("beq", "bge", "bgeu", "blt", "bltu", "bne")) {
                        val loc = getOffset() + lex[3].toInt()
                        prog.addLabel("L$loc", loc)
                        lex[3] = "L$loc"
                    }
                    val t = listOf(lex)
                    return t
                } catch (e: SimulatorError) {
                    errors.add(AssemblerError(currentLineNumber, e))
                }
            } catch (e: NumberFormatException) {
                if (c.startsWith("0x") || c.startsWith("0b") || c.matches(Regex("\\d+"))) {
                    errors.add(AssemblerError(currentLineNumber, e))
                }
            }
        }
        return listOf(tokens)
    }

    /**
     * Changes the assembler state in response to directives
     *
     * @param directive the assembler directive, starting with a "."
     * @param args any arguments following the directive
     * @param line the original line (which is needed for some directives)
     */
    private fun parseAssemblerDirective(directive: String, args: LineTokens, line: String, dbg: DebugInfo) {
        when (directive) {
            ".data" -> inTextSegment = false
            ".text" -> {
                inTextSegment = true
            }

            ".register_size" -> {
                if (!allow_custom_memory_segments) {
                    throw AssemblerError("""You can only set the register size address BEFORE any labels or
                        |instructions have been processed""".trimMargin(), dbg)
                }
                try {
                    checkArgsLength(args, 1, dbg)
                } catch (e: AssemblerError) {
                    throw AssemblerError("$directive takes in zero or one argument(s) to specify encoding!", dbg)
                }
                val instwidth = userStringToInt(args[0])
                if (!listOf(16, 32, 64, 128).contains(instwidth)) {
                    throw AssemblerError("Unknown instruction size!", dbg)
                }
                Renderer.displayWarning("Will set width to $instwidth!")
            }

            ".data_start" -> {
                if (!allow_custom_memory_segments) {
                    throw AssemblerError("""You can only set the data start address BEFORE any labels or
                        |instructions have been processed""".trimMargin(), dbg)
                }
                checkArgsLength(args, 1, dbg)
                val location = userStringToInt(args[0])
                MemorySegments.STATIC_BEGIN = location
            }

            ".byte" -> {
                for (arg in args) {
                    val byte = userStringToInt(arg)
                    if (byte !in -127..255) {
                        throw AssemblerError("invalid byte $byte too big", dbg)
                    }
                    prog.addToData(byte.toByte())
                    currentDataOffset++
                }
            }

            ".string", ".asciiz" -> {
                checkArgsLength(args, 1, dbg)
                val ascii: String = try {
                    val str = args[0]
                    if (str.length < 2 || str[0] != str[str.length - 1] || str[0] != '"') {
                        throw IllegalArgumentException()
                    }
                    unescapeString(str.drop(1).dropLast(1))
                } catch (e: Throwable) {
                    throw AssemblerError("couldn't parse ${args[0]} as a string", dbg)
                }
                for (c in ascii) {
                    if (c.toInt() !in 0..127) {
                        throw AssemblerError("unexpected non-ascii character: '$c'", dbg)
                    }
                    prog.addToData(c.toByte())
                    currentDataOffset++
                }

                /* Add NUL terminator */
                prog.addToData(0)
                currentDataOffset++
            }

            ".half" -> {
                for (arg in args) {
                    try {
                        val word = userStringToInt(arg)
                        prog.addToData(word.toByte())
                        prog.addToData((word shr 8).toByte())
                    } catch (e: NumberFormatException) {
                        /* arg is not a number; interpret as label */
                        prog.addDataRelocation(
                                prog.symbolPart(arg, dbg),
                                prog.labelOffsetPart(arg, dbg),
                                currentDataOffset - MemorySegments.STATIC_BEGIN,
                                dbg)
                        prog.addToData(0)
                        prog.addToData(0)
                    }
                    currentDataOffset += 2
                }
            }

            ".word" -> {
                for (arg in args) {
                    try {
                        val word = userStringToInt(arg)
                        prog.addToData(word.toByte())
                        prog.addToData((word shr 8).toByte())
                        prog.addToData((word shr 16).toByte())
                        prog.addToData((word shr 24).toByte())
                    } catch (e: NumberFormatException) {
                        /* arg is not a number; interpret as label */
                        prog.addDataRelocation(
                                prog.symbolPart(arg, dbg),
                                prog.labelOffsetPart(arg, dbg),
                                currentDataOffset - MemorySegments.STATIC_BEGIN,
                                dbg)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                    }
                    currentDataOffset += 4
                }
            }

            ".globl", "global" -> {
                args.forEach(prog::makeLabelGlobal)
            }

            ".import" -> {
                checkArgsLength(args, 1, dbg)
                var filepath = args[0]
                val relative = !filepath.startsWith("<")
                if (filepath.matches(Regex("\".*\"|'.*'|<.*>"))) {
                    filepath = filepath.slice(1..(filepath.length - 2))
                }
                prog.addImport(filepath, relative)
            }

            ".space" -> {
                checkArgsLength(args, 1, dbg)
                try {
                    val reps = userStringToInt(args[0])
                    for (c in 1..reps) {
                        prog.addToData(0)
                    }
                    currentDataOffset += reps
                } catch (e: NumberFormatException) {
                    throw AssemblerError("${args[0]} not a valid argument", dbg)
                }
            }

            ".align" -> {
                checkArgsLength(args, 1, dbg)
                val pow2 = userStringToInt(args[0])
                if (pow2 < 0 || pow2 > 8) {
                    throw AssemblerError(".align argument must be between 0 and 8, inclusive", dbg)
                }
                val mask = (1 shl pow2) - 1 // Sets pow2 rightmost bits to 1
                /* Add padding until data offset aligns with given power of 2 */
                while ((currentDataOffset and mask) != 0) {
                    prog.addToData(0)
                    currentDataOffset++
                }
            }

            ".equiv", ".equ", ".set" -> {
                checkArgsLength(args, 2, dbg)
                val oldDefn = prog.addEqu(args[0], args[1])
                if (directive == ".equiv" && oldDefn != null) {
                    throw AssemblerError("attempt to redefine ${args[0]}", dbg)
                }
            }

            ".float" -> {
                for (arg in args) {
                    try {
                        val float = userStringToFloat(arg)
                        val bits = float.toRawBits()
                        prog.addToData(bits.toByte())
                        prog.addToData((bits shr 8).toByte())
                        prog.addToData((bits shr 16).toByte())
                        prog.addToData((bits shr 24).toByte())
                    } catch (e: NumberFormatException) {
                        /* arg is not a number; interpret as label */
                        prog.addDataRelocation(arg, currentDataOffset - MemorySegments.STATIC_BEGIN,
                                dbg = dbg)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                    }
                    currentDataOffset += 4
                }
            }

            ".double" -> {
                for (arg in args) {
                    try {
                        val double = userStringToDouble(arg)
                        val bits = double.toRawBits()
                        prog.addToData(bits.toByte())
                        prog.addToData((bits shr 8).toByte())
                        prog.addToData((bits shr 16).toByte())
                        prog.addToData((bits shr 24).toByte())
                        prog.addToData((bits shr 32).toByte())
                        prog.addToData((bits shr 40).toByte())
                        prog.addToData((bits shr 48).toByte())
                        prog.addToData((bits shr 56).toByte())
                    } catch (e: NumberFormatException) {
                        /* arg is not a number; interpret as label */
                        prog.addDataRelocation(arg, currentDataOffset - MemorySegments.STATIC_BEGIN,
                                dbg = dbg)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                    }
                    currentDataOffset += 8
                }
            }

            else -> throw AssemblerError("unknown assembler directive $directive", dbg)
        }
    }

    fun addRelocation(relocator: Relocator, offset: Int, label: String, dbg: DebugInfo) =
            prog.addRelocation(
                    relocator, prog.symbolPart(label, dbg),
                    prog.labelOffsetPart(label, dbg), offset, dbg)
}

/**
 * Pass #2 of our two pass assembler.
 *
 * It writes TAL instructions to the program, and also adds debug info to the program.
 * @see addInstruction
 * @see venus.riscv.Program.addDebugInfo
 */

internal class AssemblerPassTwo(val prog: Program, val talInstructions: List<DebugInstruction>) {
    private val errors = ArrayList<AssemblerError>()
    private val warnings = ArrayList<AssemblerWarning>()
    fun run(): AssemblerOutput {
        resolveEquivs(prog)
        for ((dbg, inst) in talInstructions) {
            try {
                addInstruction(inst, dbg)
                prog.addDebugInfo(dbg)
                if (getImmWarning != "") {
                    val (lineNumber, _) = dbg
                    warnings.add(AssemblerWarning(lineNumber, AssemblerWarning(getImmWarning)))
                    getImmWarning = ""
                }
            } catch (e: AssemblerError) {
                val (lineNumber, _) = dbg
                if (e.errorType is InstructionNotFoundError) {
                    val cmd = getInstruction(inst)
                    // This is meant to allow for cmds with periods since the pseudodispatcher does not allow for special chars.
                    val cleanedCMD = cmd.replace(".", "")
                    val pw = try {
                        PseudoDispatcher.valueOf(cleanedCMD).pw
                    } catch (_: Throwable) {
                        errors.add(AssemblerError(lineNumber, e))
                        continue
                    }
                    try {
                        pw(inst, AssemblerPassOne("", abspath = ""), dbg)
                        errors.add(AssemblerError(lineNumber, e))
                    } catch (pe: Throwable) {
                        errors.add(AssemblerError(lineNumber, pe))
                    }
                } else {
                    errors.add(AssemblerError(lineNumber, e))
                }
            }
        }
        return AssemblerOutput(prog, errors, warnings)
    }

    /**
     * Adds machine code corresponding to our instruction to the program.
     *
     * @param tokens a list of strings corresponding to the space delimited line
     */
    private fun addInstruction(tokens: LineTokens, dbg: DebugInfo) {
        if (tokens.isEmpty() || tokens[0].isEmpty()) return
        val cmd = getInstruction(tokens)
        val inst = Instruction[cmd, dbg]
        val mcode = inst.format.fill()
        inst.parser(prog, mcode, tokens.drop(1), dbg)
        prog.add(mcode)
    }

    /** Resolve all labels in PROG defined by .equiv, .equ, or .set and add
     *  these to PROG as ordinary labels.  Checks for duplicate or
     *  conflicting definition. */
    private fun resolveEquivs(prog: Program) {
        val conflicts = prog.labels.keys.intersect(prog.equivs.keys)
        if (conflicts.isNotEmpty()) {
            throw AssemblerError("conflicting definitions for $conflicts")
        }
        val processing = HashSet<String>()
        for (equiv in prog.equivs.keys) {
            if (equiv !in prog.labels.keys) {
                prog.labels[equiv] = findDefn(equiv, prog, processing)
            }
        }
    }
    /** Return the ultimate definition of SYM, an .equ-defined symbol, in
     *  PROG, assuming that if SYM is in ACTIVE, it is part of a
     *  circular chain of definitions. */
    private fun findDefn(sym: String, prog: Program, active: HashSet<String>): Int {
        // FIXME: Global symbols not defined in this program.
        if (sym in active) {
            throw AssemblerError("circularity in definition of $sym")
        }
        val value = prog.equivs[sym]!!
        if (isNumeral(value)) {
            return userStringToInt(value)
        } else if (value in prog.labels.keys) {
            return prog.labels[value]!!
        } else if (value in prog.equivs.keys) {
            active.add(sym)
            val result = findDefn(value, prog, active)
            active.remove(sym)
            return result
        } else {
            throw AssemblerError("undefined symbol: $value")
        }
    }
}

/**
 * Gets the instruction from a line of code
 *
 * @param tokens the tokens from the current line
 * @return the instruction (aka the first argument, in lowercase)
 */
private fun getInstruction(tokens: LineTokens) = tokens[0].toLowerCase()