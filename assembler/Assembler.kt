package venusbackend.assembler
/* ktlint-disable no-wildcard-imports */
import venusbackend.riscv.InitInstructions
import venusbackend.assembler.pseudos.checkArgsLength
import venus.Renderer
import venusbackend.riscv.*
import venusbackend.riscv.insts.InstructionNotFoundError
import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.getImmWarning
import venusbackend.riscv.insts.dsl.relocators.Relocator
import venusbackend.simulator.SimulatorError
/* ktlint-enable no-wildcard-imports */

/**
 * This singleton implements a simple two-pass venusbackend.assembler to transform files into programs.
 */
object Assembler {
    /**
     * Assembles the given code into an unlinked Program.
     *
     * @param text the code to assemble.
     * @return an unlinked program.
     * @see venus.linker.Linker
     * @see venus.simulator.Simulator
     */
    fun assemble(text: String, name: String = "anonymous"): AssemblerOutput {
        InitInstructions() // This is due to how some method of compilation handle all of the code.
        var (passOneProg, talInstructions, passOneErrors) = AssemblerPassOne(text.replace("\r", ""), name).run()

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
                val pone = AssemblerPassOne(text).run()
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
        return passTwoOutput
    }
}

data class DebugInfo(val lineNo: Int, val line: String)
data class DebugInstruction(val debug: DebugInfo, val LineTokens: List<String>)
data class PassOneOutput(
    val prog: Program,
    val talInstructions: List<DebugInstruction>,
    val errors: List<AssemblerError>
)
data class AssemblerOutput(val prog: Program, val errors: List<AssemblerError>, val warnings: List<AssemblerWarning>)

/**
 * Pass #1 of our two pass venusbackend.assembler.
 *
 * It parses labels, expands pseudo-instructions and follows venusbackend.assembler directives.
 * It populations [talInstructions], which is then used by [AssemblerPassTwo] in order to actually assemble the code.
 */
internal class AssemblerPassOne(private val text: String, name: String = "anonymous") {
    /** The program we are currently assembling */
    private val prog = Program(name)
    /** The text offset where the next instruction will be written */
    private var currentTextOffset = 0 // MemorySegments.TEXT_BEGIN
    /** The data offset where more data will be written */
    private var currentDataOffset = MemorySegments.STATIC_BEGIN
    /** The allows user to set custom memory segments until the venusbackend.assembler has used an offset. */
    private var allow_custom_memory_setments = true
    /** Whether or not we are currently in the text segment */
    private var inTextSegment = true
    /** TAL Instructions which will be added to the program */
    private val talInstructions = ArrayList<DebugInstruction>()
    /** The current line number (for user-friendly errors) */
    private var currentLineNumber = 0
    /** List of all errors encountered */
    private val errors = ArrayList<AssemblerError>()
    private val warnings = ArrayList<AssemblerWarning>()

    fun run(): PassOneOutput {
        doPassOne()
        return PassOneOutput(prog, talInstructions, errors)
    }

    private fun doPassOne() {
        for (line in text.split('\n')) {
            try {
                currentLineNumber++

                val offset = getOffset()

                val (labels, args) = Lexer.lexLine(line)
                for (label in labels) {
                    allow_custom_memory_setments = false
                    val oldOffset = prog.addLabel(label, offset)
                    if (oldOffset != null) {
                        throw AssemblerError("label $label defined twice")
                    }
                }

                if (args.isEmpty() || args[0].isEmpty()) continue // empty line

                if (isAssemblerDirective(args[0])) {
                    parseAssemblerDirective(args[0], args.drop(1), line)
                } else {
                    allow_custom_memory_setments = false
                    val expandedInsts = replacePseudoInstructions(args)
                    for (inst in expandedInsts) {
                        val dbg = DebugInfo(currentLineNumber, line)
                        val instsize = try {
                            Instruction[getInstruction(inst)].format.length
                        } catch (e: AssemblerError) {
                            4
                        }
                        talInstructions.add(DebugInstruction(dbg, inst))
                        currentTextOffset += instsize
                    }
                }
            } catch (e: AssemblerError) {
                errors.add(AssemblerError(currentLineNumber, e))
            }
        }
    }

    /** Gets the current offset (either text or data) depending on where we are writing */
    fun getOffset() = if (inTextSegment) currentTextOffset else currentDataOffset

    /**
     * Determines if the given token is an venusbackend.assembler directive
     *
     * @param cmd the token to check
     * @return true if the token is an venusbackend.assembler directive
     * @see parseAssemblerDirective
     */
    private fun isAssemblerDirective(cmd: String) = cmd.startsWith(".")

    /**
     * Replaces any pseudoinstructions which occur in our program.
     *
     * @param tokens a list of strings corresponding to the space delimited line
     * @return the corresponding TAL instructions (possibly unchanged)
     */
    private fun replacePseudoInstructions(tokens: LineTokens): List<LineTokens> {
        try {
            val cmd = getInstruction(tokens)
            // This is meant to allow for cmds with periods since the pseudodispatcher does not allow for special chars.
            val cleanedCMD = cmd.replace(".", "")
            val pw = PseudoDispatcher.valueOf(cleanedCMD).pw
            return pw(tokens, this)
        } catch (t: Throwable) {
            /* TODO: don't use throwable here */
            /* not a pseudoinstruction, or expansion failure */
            val linetokens = parsePossibleMachineCode(tokens)
            return linetokens
        }
    }

    private fun parsePossibleMachineCode(tokens: LineTokens): List<LineTokens> {
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
                    val lex = Lexer.lexLine(decoded).second.toMutableList()
                    if (lex[0] == "jal") {
                        val loc = getOffset() + lex[2].toInt()
                        prog.addLabel("L" + loc.toString(), loc)
                        lex[2] = "L" + loc.toString()
                    }
                    if (lex[0] in listOf("beq", "bge", "bgeu", "blt", "bltu", "bne")) {
                        val loc = getOffset() + lex[3].toInt()
                        prog.addLabel("L" + loc.toString(), loc)
                        lex[3] = "L" + loc.toString()
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
     * Changes the venusbackend.assembler state in response to directives
     *
     * @param directive the venusbackend.assembler directive, starting with a "."
     * @param args any arguments following the directive
     * @param line the original line (which is needed for some directives)
     */
    private fun parseAssemblerDirective(directive: String, args: LineTokens, line: String) {
        when (directive) {
            ".data" -> inTextSegment = false
            ".text" -> {
                inTextSegment = true
            }

            ".register_size" -> {
                if (!allow_custom_memory_setments) {
                    throw AssemblerError("""You can only set the register size address BEFORE any labels or
                        |instructions have been processed""".trimMargin())
                }
                try {
                    checkArgsLength(args, 1)
                } catch (e: AssemblerError) {
                    throw AssemblerError("$directive takes in zero or one argument(s) to specify encoding!")
                }
                val instwidth = userStringToInt(args[0])
                if (!listOf(16, 32, 64, 128).contains(instwidth)) {
                    throw AssemblerError("Unknown instruction size!")
                }
                Renderer.displayWarning("Will set width to $instwidth!")
            }

            ".data_start" -> {
                if (!allow_custom_memory_setments) {
                    throw AssemblerError("""You can only set the data start address BEFORE any labels or
                        |instructions have been processed""".trimMargin())
                }
                checkArgsLength(args, 1)
                val location = userStringToInt(args[0])
                MemorySegments.STATIC_BEGIN = location
            }

            ".byte" -> {
                for (arg in args) {
                    val byte = userStringToInt(arg)
                    if (byte !in -127..255) {
                        throw AssemblerError("invalid byte $byte too big")
                    }
                    prog.addToData(byte.toByte())
                    currentDataOffset++
                }
            }

            ".string", ".asciiz" -> {
                checkArgsLength(args, 1)
                val ascii: String = try {
                    val str = args[0]
                    if (str.length < 2 || str[0] != str[str.length - 1] || str[0] != '"') {
                        throw IllegalArgumentException()
                    }
                    unescapeString(str.drop(1).dropLast(1))
                } catch (e: Throwable) {
                    throw AssemblerError("couldn't parse ${args[0]} as a string")
                }
                for (c in ascii) {
                    if (c.toInt() !in 0..127) {
                        throw AssemblerError("unexpected non-ascii character: $c")
                    }
                    prog.addToData(c.toByte())
                    currentDataOffset++
                }

                /* Add NUL terminator */
                prog.addToData(0)
                currentDataOffset++
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
                        prog.addDataRelocation(arg, currentDataOffset - MemorySegments.STATIC_BEGIN)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                        prog.addToData(0)
                    }
                    currentDataOffset += 4
                }
            }

            ".globl" -> {
                args.forEach(prog::makeLabelGlobal)
            }

            ".import" -> {
                checkArgsLength(args, 1)
                var filepath = args[0]
                if (filepath.matches(Regex("\".*\"|'.*'"))) {
                    filepath = filepath.slice(1..(filepath.length - 2))
                }
                prog.addImport(filepath)
            }

            ".align" -> {
                checkArgsLength(args, 1)
                val pow2 = userStringToInt(args[0])
                if (pow2 < 0 || pow2 > 8) {
                    throw AssemblerError(".align argument must be between 0 and 8, inclusive")
                }
                val mask = (1 shl pow2) - 1 // Sets pow2 rightmost bits to 1
                /* Add padding until data offset aligns with given power of 2 */
                while ((currentDataOffset and mask) != 0) {
                    prog.addToData(0)
                    currentDataOffset++
                }
            }

            ".float" -> {
                checkArgsLength(args, 1)
                val value = args[0]
                val float = userStringToFloat(value)
                val bits = float.toRawBits()
                prog.addToData(bits.toByte())
                prog.addToData((bits shr 8).toByte())
                prog.addToData((bits shr 16).toByte())
                prog.addToData((bits shr 24).toByte())
                currentDataOffset += 4
            }

            ".double" -> {
                checkArgsLength(args, 1)
                val value = args[0]
                val double = userStringToDouble(value)
                val bits = double.toRawBits()
                prog.addToData(bits.toByte())
                prog.addToData((bits shr 8).toByte())
                prog.addToData((bits shr 16).toByte())
                prog.addToData((bits shr 24).toByte())
                prog.addToData((bits shr 32).toByte())
                prog.addToData((bits shr 40).toByte())
                prog.addToData((bits shr 48).toByte())
                prog.addToData((bits shr 56).toByte())
                currentDataOffset += 8
            }

            else -> throw AssemblerError("unknown venusbackend.assembler directive $directive")
        }
    }

    fun addRelocation(relocator: Relocator, offset: Int, label: String) =
            prog.addRelocation(relocator, label, offset)
}

/**
 * Pass #2 of our two pass venusbackend.assembler.
 *
 * It writes TAL instructions to the program, and also adds debug info to the program.
 * @see addInstruction
 * @see venus.riscv.Program.addDebugInfo
 */
internal class AssemblerPassTwo(val prog: Program, val talInstructions: List<DebugInstruction>) {
    private val errors = ArrayList<AssemblerError>()
    private val warnings = ArrayList<AssemblerWarning>()
    fun run(): AssemblerOutput {
        for ((dbg, inst) in talInstructions) {
            try {
                addInstruction(inst)
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
                        pw(inst, AssemblerPassOne(""))
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
    private fun addInstruction(tokens: LineTokens) {
        if (tokens.isEmpty() || tokens[0].isEmpty()) return
        val cmd = getInstruction(tokens)
        val inst = Instruction[cmd]
        val mcode = inst.format.fill()
        inst.parser(prog, mcode, tokens.drop(1))
        prog.add(mcode)
    }
}

/**
 * Gets the instruction from a line of code
 *
 * @param tokens the tokens from the current line
 * @return the instruction (aka the first argument, in lowercase)
 */
private fun getInstruction(tokens: LineTokens) = tokens[0].toLowerCase()
