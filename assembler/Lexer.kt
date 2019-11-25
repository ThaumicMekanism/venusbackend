package venusbackend.assembler

typealias LineTokens = List<String>

/**
 * A singleton which can be used to lex a given line.
 */
object Lexer {
    private val charPatn = """'(?:\\.|[^\\'])'"""
    private val strPatn = "\"(?:\\\\.|[^\\\\\"\"])*?\""
    private val otherTokenPatn = """[^:() \t,#""']+"""
    private val tokenPatn = "($charPatn|$strPatn|$otherTokenPatn)"
    private val labelPatn = "($otherTokenPatn)\\s*:"
    private val baseRegPatn = """\(\s*($otherTokenPatn)\s*\)"""
    private val tokenRE =
            Regex("""(#.*)|$labelPatn|$tokenPatn|$baseRegPatn|(['""])""")

    fun lexLine(line: String): Pair<LineTokens, LineTokens> {
        val labels = ArrayList<String>()
        val insnTokens = ArrayList<String>()

        for (mat in tokenRE.findAll(line)) {
            val groups = mat.groups
            when {
                groups[1] != null -> Unit
                groups[2] != null && !insnTokens.isEmpty() -> {
                    throw AssemblerError("label ${groups[2]!!.value} in the middle of an instruction")
                }
                groups[2] != null -> labels.add(groups[2]!!.value)
                groups[3] != null -> insnTokens.add(groups[3]!!.value)
                groups[4] != null -> {
                    insnTokens.add("(" + groups[4]!!.value + ")")
                }
                else -> throw AssemblerError("unclosed string")
            }
        }
        return Pair(labels, insnTokens)
    }

    private fun addNonemptyWord(previous: ArrayList<String>, next: StringBuilder) {
        val word = next.toString()
        if (word.isNotEmpty()) {
            previous += word
        }
    }

    /**
     * Lex a line into a label (if there) and a list of arguments.
     *
     * @param line the line to lex
     * @return a pair containing the label and tokens
     * @see LineTokens
     */
//    fun lexLine(line: String): Pair<LineTokens, LineTokens> {
//        var currentWord = StringBuilder("")
//        val previousWords = ArrayList<String>()
//        val labels = ArrayList<String>()
//        var escaped = false
//        var inCharacter = false
//        var inString = false
//        var foundComment = false
//        var inParen = 0
//
//        for (ch in line) {
//            var wasDelimiter = false
//            var wasLabel = false
//            when (ch) {
//                '#' -> foundComment = !inString && !inCharacter
//                '\'' -> inCharacter = !(escaped xor inCharacter) && !inString
//                '"' -> inString = !(escaped xor inString) && !inCharacter
//                ':' -> {
//                    if (!inString && !inCharacter) {
//                        wasLabel = true
//                        if (previousWords.isNotEmpty()) {
//                            throw AssemblerError("label $currentWord in the middle of an instruction")
//                        }
//                    }
//                }
//                '(' -> {
//                    inParen++
//                    wasDelimiter = !inString && !inCharacter
//                }
//                ')' -> {
//                    if (inParen == 0) {
//                        throw AssemblerError("Cannot end a parentheses sequence which has not started yet.")
//                    }
//                    inParen--
//                    wasDelimiter = !inString && !inCharacter
//                }
//                ' ', '\t', ',' -> wasDelimiter = !inString && !inCharacter
//            }
//            escaped = !escaped && ch == '\\'
//
//            if (foundComment) break
//
//            if (wasDelimiter) {
//                addNonemptyWord(previousWords, currentWord)
//                currentWord = StringBuilder("")
//            } else if (wasLabel) {
//                addNonemptyWord(labels, currentWord)
//                currentWord = StringBuilder("")
//            } else {
//                currentWord.append(ch)
//            }
//        }
//        if (inParen > 0) {
//            throw AssemblerError("Mismatched number of parentheses!")
//        }
//        addNonemptyWord(previousWords, currentWord)
//
//        return Pair(labels, previousWords)
//    }
    /**
     * Lex a line into a label (if there) and a list of arguments.
     *
     * @param line the line to lex
     * @return tokens
     * @see LineTokens
     */
    fun lex(line: String): LineTokens {
        var currentWord = StringBuilder("")
        val previousWords = ArrayList<String>()
        var escaped = false
        var inCharacter = false
        var inString = false

        for (ch in line) {
            var wasDelimiter = false
            when (ch) {
                '\'' -> inCharacter = !(escaped xor inCharacter) && !inString
                '"' -> inString = !(escaped xor inString) && !inCharacter
                ' ' -> wasDelimiter = !inString && !inCharacter
//                ' ', '\t', '(', ')', ',' -> wasDelimiter = !inString && !inCharacter
            }
            escaped = !escaped && ch == '\\'

            if (wasDelimiter) {
                val l = currentWord.length
                if (l > 2 && (currentWord[0].toString() in listOf("'", "\"") && currentWord[l - 1] == currentWord[0])) {
                    val cs = currentWord.subSequence(1, l - 1)
                    currentWord = StringBuilder(cs)
                }
                addNonemptyWord(previousWords, currentWord)
                currentWord = StringBuilder("")
            } else if (escaped) {} else {
                currentWord.append(ch)
            }
        }
        val l = currentWord.length
        if (l > 2 && (currentWord[0].toString() in listOf("'", "\"") && currentWord[l - 1] == currentWord[0])) {
            val cs = currentWord.subSequence(1, l - 1)
            currentWord = StringBuilder(cs)
        }
        addNonemptyWord(previousWords, currentWord)

        return previousWords
    }
}
