package venusbackend.riscv.insts.dsl

import venusbackend.assembler.AssemblerError
import venusbackend.numbers.QuadWord
import venusbackend.riscv.userStringToInt
import kotlin.math.ceil
import kotlin.math.log2

/**
 * Gets the immediate from a string, checking if it is in range.
 *
 * @param str the immediate as a string
 * @param min the minimum allowable value of the immediate
 * @param max the maximum allowable value of the immediate
 * @return the immediate, as an integer
 *
 * @throws IllegalArgumentException if the wrong number of arguments is given
 */
var getImmWarning = ""
internal fun getImmediate(str: String, min: Int, max: Int): Int {
    var imm = try {
        userStringToInt(str)
    } catch (e: NumberFormatException) {
        val hint = when {
            str.length > 4 -> " (might be too large)"
            else -> ""
        }
        throw AssemblerError("invalid number, got $str$hint")
    }

    if (imm !in min..max) {
        val imm_range = max - min + 1
        if (min < 0 && imm > 0 && imm < imm_range) {
            val topbit = imm and (1 shl (ceil(log2(imm_range.toDouble())).toInt() - 1))
            val mask = topbit.inv()
            imm = imm and mask
            imm -= topbit
            getImmWarning = """The value that was given was larger than the max allowed value ($max) but within a valid unsigned range (0 to ${imm_range - 1}) so it will be interpreted just as two's complement bits ($imm)."""
        } else {
            val largeimm = if (min < 0 && imm > 0) " or between 0 and $imm_range to fill the bits using two's complement" else ""
            throw AssemblerError("immediate $str (= $imm) out of range (should be between $min and $max$largeimm)")
        }
    }

    return imm
}

internal fun compareUnsignedShort(v1: Short, v2: Short): Short {
    return ((v1.toInt() xor Short.MIN_VALUE.toInt()).toShort()).compareTo((v2.toInt() xor Short.MIN_VALUE.toInt()).toShort()).toShort()
}

internal fun compareUnsigned(v1: Int, v2: Int): Int {
    return (v1 xor Int.MIN_VALUE).compareTo(v2 xor Int.MIN_VALUE)
}

internal fun compareUnsignedLong(v1: Long, v2: Long): Int {
    return (v1 xor Long.MIN_VALUE).compareTo(v2 xor Long.MIN_VALUE)
}

internal fun compareUnsignedQuadWord(v1: QuadWord, v2: QuadWord): Int {
    return (v1 xor QuadWord.MIN_VALUE).compareTo(v2 xor QuadWord.MIN_VALUE)
}
