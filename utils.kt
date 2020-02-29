package venusbackend

/** a map from integers to the corresponding hex digits */
private val hexMap = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F')

/**
 * Converts a value to a two's complement hex number.
 *
 * By two's complement, I mean that -1 becomes 0xFFFFFFFF not -0x1.
 *
 * @param value the value to convert
 * @return the hexadecimal string corresponding to that value
 */
fun toHex(value: Int, num_nibbles: Int = 8, add_prefix: Boolean = true): String {
    var remainder = value.toLong()
    var suffix = ""

    repeat(num_nibbles) {
        val hexDigit = hexMap[(remainder and 15).toInt()]
        suffix = hexDigit + suffix
        remainder = remainder ushr 4
    }

    if (add_prefix) {
        suffix = "0x" + suffix
    }

    return suffix
}

fun toHex(value: Number): String {
    return toHex(value.toInt())
}