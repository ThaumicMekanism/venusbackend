package venusbackend.assembler

typealias LineTokens = List<String>

/**
 * A singleton which can be used to lex a given line.
 */
object Lexer {
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
    fun lexLine(line: String): Pair<LineTokens, LineTokens> {
        var currentWord = StringBuilder("")
        val previousWords = ArrayList<String>()
        val labels = ArrayList<String>()
        var escaped = false
        var inCharacter = false
        var inString = false
        var foundComment = false

        for (ch in line) {
            var wasDelimiter = false
            var wasLabel = false
            when (ch) {
                '#' -> foundComment = !inString && !inCharacter
                '\'' -> inCharacter = !(escaped xor inCharacter) && !inString
                '"' -> inString = !(escaped xor inString) && !inCharacter
                ':' -> {
                    if (!inString && !inCharacter) {
                        wasLabel = true
                        if (previousWords.isNotEmpty()) {
                            throw AssemblerError("label $currentWord in the middle of an instruction")
                        }
                    }
                }
                ' ', '\t', '(', ')', ',' -> wasDelimiter = !inString && !inCharacter
            }
            escaped = !escaped && ch == '\\'

            if (foundComment) break

            if (wasDelimiter) {
                addNonemptyWord(previousWords, currentWord)
                currentWord = StringBuilder("")
            } else if (wasLabel) {
                addNonemptyWord(labels, currentWord)
                currentWord = StringBuilder("")
            } else {
                currentWord.append(ch)
            }
        }

        addNonemptyWord(previousWords, currentWord)

        return Pair(labels, previousWords)
    }
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
