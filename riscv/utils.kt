package venusbackend.riscv

fun userStringToInt(s: String): Int {
    if (isCharacterLiteral(s)) {
        return characterLiteralToInt(s)
    }

    val radix = when {
        s.startsWith("0x") -> 16
        s.startsWith("0b") -> 2
        s.drop(1).startsWith("0x") -> 16
        s.drop(1).startsWith("0b") -> 2
        else -> return s.toLong().toInt()
    }

    val skipSign = when (s.first()) {
        '+', '-' -> 1
        else -> 0
    }

    val noRadixString = s.take(skipSign) + s.drop(skipSign + 2)
    return noRadixString.toLong(radix).toInt()
}

fun userStringToLong(s: String): Long {
    if (isCharacterLiteral(s)) {
        return characterLiteralToLong(s)
    }

    val radix = when {
        s.startsWith("0x") -> 16
        s.startsWith("0b") -> 2
        s.drop(1).startsWith("0x") -> 16
        s.drop(1).startsWith("0b") -> 2
        else -> return s.toLong()
    }

    val skipSign = when (s.first()) {
        '+', '-' -> 1
        else -> 0
    }

    val noRadixString = s.take(skipSign) + s.drop(skipSign + 2)
    return noRadixString.toLong(radix)
}

fun userStringToFloat(s: String): Float {
    val radix = when {
        s.startsWith("0x") -> 16
        s.startsWith("0b") -> 2
        s.drop(1).startsWith("0x") -> 16
        s.drop(1).startsWith("0b") -> 2
        else -> return s.toFloat()
    }
    var bits = userStringToInt(s)
    val v = Float.fromBits(bits)
    return v
}

fun userStringToDouble(s: String): Double {
    val radix = when {
        s.startsWith("0x") -> 16
        s.startsWith("0b") -> 2
        s.drop(1).startsWith("0x") -> 16
        s.drop(1).startsWith("0b") -> 2
        else -> return s.toDouble()
    }
    var bits = userStringToLong(s)
    val v = Double.fromBits(bits)
    return v
}

fun getBit(value: Number, position: Int): Int {
    return ((value.toLong() shr position) and 1).toInt()
}

fun unescapeString(s: String) =
        s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\b", "\b")
                .replace("\\\"", "\"")
                .replace("\\'", "'")

private fun isCharacterLiteral(s: String) =
        s.first() == '\'' && s.last() == '\''

private fun characterLiteralToInt(s: String): Int {
    val stripSingleQuotes = s.drop(1).dropLast(1)
    if (stripSingleQuotes == "\\'") return '\''.toInt()
    if (stripSingleQuotes == "\"") return '"'.toInt()

    try {
        val parsed = unescapeString(stripSingleQuotes)
        if (parsed.isEmpty()) throw NumberFormatException("character literal $s is empty")
        if (parsed.length > 1) throw NumberFormatException("character literal $s too long")
        return parsed[0].toInt()
    } catch (e: Throwable) {
        throw NumberFormatException("could not parse character literal $s")
    }
}

private fun characterLiteralToLong(s: String): Long {
    val stripSingleQuotes = s.drop(1).dropLast(1)
    if (stripSingleQuotes == "\\'") return '\''.toLong()
    if (stripSingleQuotes == "\"") return '"'.toLong()

    try {
        val parsed = unescapeString(stripSingleQuotes)
        if (parsed.isEmpty()) throw NumberFormatException("character literal $s is empty")
        if (parsed.length > 1) throw NumberFormatException("character literal $s too long")
        return parsed[0].toLong()
    } catch (e: Throwable) {
        throw NumberFormatException("could not parse character literal $s")
    }
}
