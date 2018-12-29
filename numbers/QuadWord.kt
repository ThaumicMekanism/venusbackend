package venusbackend.numbers

import kotlin.experimental.and
import kotlin.math.sign

class QuadWord(var int0: Int = 0, var int1: Int = 0, var int2: Int = 0, var int3: Int = 0) : Number(), Comparable<QuadWord> {
    companion object {
        /**
         * The number of bytes used to represent an instance of Int in a binary form.
         */
        public const val SIZE_BYTES: Int = 16

        /**
         * The number of bits used to represent an instance of Int in a binary form.
         */
        public const val SIZE_BITS: Int = 128
        /**
         * A constant holding the minimum value an instance of Int can have.
         */
        public val MIN_VALUE: QuadWord = QuadWord(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        /**
         * A constant holding the maximum value an instance of Int can have.
         */
        public val MAX_VALUE: QuadWord = QuadWord(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
    }

    override fun compareTo(other: QuadWord): Int {
        val int3comp = this.int3.compareTo(other.int3)
        if (int3comp == 0) {
            val int2comp = this.int2.compareTo(other.int2)
            if (int2comp == 0) {
                val int1comp = this.int1.compareTo(other.int1)
                if (int1comp == 0) {
                    return this.int0.compareTo(other.int0)
                }
                return int1comp
            }
            return int2comp
        }
        return int3comp
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    public operator fun compareTo(other: Byte): Int {
        return this.toByte().compareTo(other)
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    public operator fun compareTo(other: Short): Int {
        return this.toShort().compareTo(other)
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    public operator fun compareTo(other: Int): Int {
        return this.toInt().compareTo(other)
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    public operator fun compareTo(other: Long): Int {
        return this.toLong().compareTo(other)
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    public operator fun compareTo(other: Float): Int {
        return this.toFloat().compareTo(other)
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    public operator fun compareTo(other: Double): Int {
        return this.toDouble().compareTo(other)
    }

    /** Adds the other value to this value. */
    public operator fun plus(other: Byte): QuadWord {
        return this.plus(other.toQuadWord())
    }
    /** Adds the other value to this value. */
    public operator fun plus(other: Short): QuadWord {
        return this.plus(other.toQuadWord())
    }
    /** Adds the other value to this value. */
    public operator fun plus(other: Int): QuadWord {
        return this.plus(other.toQuadWord())
    }
    /** Adds the other value to this value. */
    public operator fun plus(other: Long): QuadWord {
        return this.plus(other.toQuadWord())
    }
    /** Adds the other value to this value. */
    public operator fun plus(other: Float): Float {
        return this.toFloat() + other
    }
    /** Adds the other value to this value. */
    public operator fun plus(other: Double): Double {
        return this.toDouble() + other
    }
    /** Adds the other value to this value. */
    public operator fun plus(other: QuadWord): QuadWord {
        val long0 = this.int0.toLong() + other.int0.toLong()
        val long1 = this.int1.toLong() + other.int1.toLong() + (long0 ushr 32)
        val long2 = this.int2.toLong() + other.int2.toLong() + (long1 ushr 32)
        val long3 = this.int3.toLong() + other.int3.toLong() + (long2 ushr 32)
        return QuadWord(long0.toInt(), long1.toInt(), long2.toInt(), long3.toInt())
    }

    /** Subtracts the other value from this value. */
    public operator fun minus(other: Byte): QuadWord {
        return this.minus(other.toQuadWord())
    }
    /** Subtracts the other value from this value. */
    public operator fun minus(other: Short): QuadWord {
        return this.minus(other.toQuadWord())
    }
    /** Subtracts the other value from this value. */
    public operator fun minus(other: Int): QuadWord {
        return this.minus(other.toQuadWord())
    }
    /** Subtracts the other value from this value. */
    public operator fun minus(other: Long): QuadWord {
        return this.minus(other.toQuadWord())
    }
    /** Subtracts the other value from this value. */
    public operator fun minus(other: Float): Float {
        return this.toFloat() - other
    }
    /** Subtracts the other value from this value. */
    public operator fun minus(other: Double): Double {
        return this.toDouble() - other
    }
    /** Subtracts the other value from this value. */
    public operator fun minus(other: QuadWord): QuadWord {
        throw NotImplementedError()
    }

    /** Multiplies this value by the other value. */
    public operator fun times(other: Byte): QuadWord {
        return this.times(other.toQuadWord())
    }
    /** Multiplies this value by the other value. */
    public operator fun times(other: Short): QuadWord {
        return this.times(other.toQuadWord())
    }
    /** Multiplies this value by the other value. */
    public operator fun times(other: Int): QuadWord {
        return this.times(other.toQuadWord())
    }
    /** Multiplies this value by the other value. */
    public operator fun times(other: Long): QuadWord {
        return this.times(other.toQuadWord())
    }
    /** Multiplies this value by the other value. */
    public operator fun times(other: Float): Float {
        return this.toFloat() * other
    }
    /** Multiplies this value by the other value. */
    public operator fun times(other: Double): Double {
        return this.toDouble() * other
    }
    /** Multiplies this value by the other value. */
    public operator fun times(other: QuadWord): QuadWord {
        throw NotImplementedError()
    }

    /** Divides this value by the other value. */
    public operator fun div(other: Byte): QuadWord {
        return this.div(other.toQuadWord())
    }
    /** Divides this value by the other value. */
    public operator fun div(other: Short): QuadWord {
        return this.div(other.toQuadWord())
    }
    /** Divides this value by the other value. */
    public operator fun div(other: Int): QuadWord {
        return this.div(other.toQuadWord())
    }
    /** Divides this value by the other value. */
    public operator fun div(other: Long): QuadWord {
        return this.div(other.toQuadWord())
    }
    /** Divides this value by the other value. */
    public operator fun div(other: Float): Float {
        return this.toFloat() / other
    }
    /** Divides this value by the other value. */
    public operator fun div(other: Double): Double {
        return this.toDouble() / other
    }
    /** Divides this value by the other value. */
    public operator fun div(other: QuadWord): QuadWord {
        throw NotImplementedError()
    }

    /** Calculates the remainder of dividing this value by the other value. */
    public operator fun rem(other: Byte): QuadWord {
        return this.rem(other.toQuadWord())
    }
    /** Calculates the remainder of dividing this value by the other value. */
    public operator fun rem(other: Short): QuadWord {
        return this.rem(other.toQuadWord())
    }
    /** Calculates the remainder of dividing this value by the other value. */
    public operator fun rem(other: Int): QuadWord {
        return this.rem(other.toQuadWord())
    }
    /** Calculates the remainder of dividing this value by the other value. */
    public operator fun rem(other: Long): QuadWord {
        return this.rem(other.toQuadWord())
    }
    /** Calculates the remainder of dividing this value by the other value. */
    public operator fun rem(other: Float): Float {
        return this.toFloat().rem(other)
    }
    /** Calculates the remainder of dividing this value by the other value. */
    public operator fun rem(other: Double): Double {
        return this.toDouble().rem(other)
    }

    /** Calculates the remainder of dividing this value by the other value. */
    public operator fun rem(other: QuadWord): QuadWord {
        throw NotImplementedError()
    }

    /** Increments this value. */
    public operator fun inc(): QuadWord {
        return this.plus(1)
    }
    /** Decrements this value. */
    public operator fun dec(): QuadWord {
        return this.minus(1)
    }
    /** Returns this value. */
    public operator fun unaryPlus(): QuadWord {
        return this
    }
    /** Returns the negative of this value. */
    public operator fun unaryMinus(): QuadWord {
        return 0.toQuadWord() - this
    }

    /** Shifts this value left by the [bitCount] number of bits. */
    public infix fun shl(bitCount: Int): QuadWord {
        val qw = this.copy()
        if (bitCount >= 64) {
            return QuadWord(0, 0, 0, 0)
        }
        if (bitCount and 0b1 == 1) {
            val msb0 = (qw.int0 shr 31)
            val msb1 = (qw.int1 shr 31)
            val msb2 = (qw.int2 shr 31)
            qw.int0 = (qw.int0 shl 1)
            qw.int1 = (qw.int1 shl 1) and msb0
            qw.int2 = (qw.int2 shl 1) and msb1
            qw.int3 = (qw.int2 shl 1) and msb2
        }
        if (bitCount % 2 == 0) {
            val msb0 = (qw.int0 shr 30)
            val msb1 = (qw.int1 shr 30)
            val msb2 = (qw.int2 shr 30)
            qw.int0 = (qw.int0 shl 2)
            qw.int1 = (qw.int1 shl 2) and msb0
            qw.int2 = (qw.int2 shl 2) and msb1
            qw.int3 = (qw.int2 shl 2) and msb2
        }
        if (bitCount % 4 == 0) {
            val msb0 = (qw.int0 shr 28)
            val msb1 = (qw.int1 shr 28)
            val msb2 = (qw.int2 shr 28)
            qw.int0 = (qw.int0 shl 4)
            qw.int1 = (qw.int1 shl 4) and msb0
            qw.int2 = (qw.int2 shl 4) and msb1
            qw.int3 = (qw.int2 shl 4) and msb2
        }
        if (bitCount % 8 == 0) {
            val msb0 = (qw.int0 shr 24)
            val msb1 = (qw.int1 shr 24)
            val msb2 = (qw.int2 shr 24)
            qw.int0 = (qw.int0 shl 8)
            qw.int1 = (qw.int1 shl 8) and msb0
            qw.int2 = (qw.int2 shl 8) and msb1
            qw.int3 = (qw.int2 shl 8) and msb2
        }
        if (bitCount % 16 == 0) {
            val msb0 = (qw.int0 shr 16)
            val msb1 = (qw.int1 shr 16)
            val msb2 = (qw.int2 shr 16)
            qw.int0 = (qw.int0 shl 16)
            qw.int1 = (qw.int1 shl 16) and msb0
            qw.int2 = (qw.int2 shl 16) and msb1
            qw.int3 = (qw.int2 shl 16) and msb2
        }
        if (bitCount % 32 == 0) {
            qw.int3 = qw.int2
            qw.int2 = qw.int1
            qw.int1 = qw.int0
            qw.int0 = 0
        }
        return qw
    }
    /** Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with copies of the sign bit. */
    public infix fun shr(bitCount: Int): QuadWord {
        val qw = this.copy()
        if (bitCount >= 64) {
            return if (this.int3.sign == 1) {
                QuadWord(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
            } else {
                QuadWord(0, 0, 0, 0)
            }
        }
        if (bitCount and 0b1 == 1) {
            val lsb1 = (qw.int1 and 0x1) shl 31
            val lsb2 = (qw.int2 and 0x1) shl 31
            val lsb3 = (qw.int3 and 0x1) shl 31
            qw.int0 = (qw.int0 shr 1) and lsb1
            qw.int1 = (qw.int1 shr 1) and lsb2
            qw.int2 = (qw.int2 shr 1) and lsb3
            qw.int3 = (qw.int2 shr 1)
        }
        if (bitCount % 2 == 0) {
            val lsb1 = (qw.int1 and 0b11) shl 30
            val lsb2 = (qw.int2 and 0b11) shl 30
            val lsb3 = (qw.int3 and 0b11) shl 30
            qw.int0 = (qw.int0 shr 2) and lsb1
            qw.int1 = (qw.int1 shr 2) and lsb2
            qw.int2 = (qw.int2 shr 2) and lsb3
            qw.int3 = (qw.int2 shr 2)
        }
        if (bitCount % 4 == 0) {
            val lsb1 = (qw.int1 and 0b1111) shl 28
            val lsb2 = (qw.int2 and 0b1111) shl 28
            val lsb3 = (qw.int3 and 0b1111) shl 28
            qw.int0 = (qw.int0 shr 4) and lsb1
            qw.int1 = (qw.int1 shr 4) and lsb2
            qw.int2 = (qw.int2 shr 4) and lsb3
            qw.int3 = (qw.int2 shr 4)
        }
        if (bitCount % 8 == 0) {
            val lsb1 = (qw.int1 and 0b11111111) shr 24
            val lsb2 = (qw.int2 and 0b11111111) shr 24
            val lsb3 = (qw.int3 and 0b11111111) shr 24
            qw.int0 = (qw.int0 shr 8) and lsb1
            qw.int1 = (qw.int1 shr 8) and lsb2
            qw.int2 = (qw.int2 shr 8) and lsb3
            qw.int3 = (qw.int2 shr 8)
        }
        if (bitCount % 16 == 0) {
            val lsb1 = (qw.int1 and 0b1111111111111111) shr 16
            val lsb2 = (qw.int2 and 0b1111111111111111) shr 16
            val lsb3 = (qw.int3 and 0b1111111111111111) shr 16
            qw.int0 = (qw.int0 shr 16) and lsb1
            qw.int1 = (qw.int1 shr 16) and lsb2
            qw.int2 = (qw.int2 shr 16) and lsb3
            qw.int3 = (qw.int2 shr 16)
        }
        if (bitCount % 32 == 0) {
            qw.int0 = qw.int1
            qw.int1 = qw.int2
            qw.int2 = qw.int3
            qw.int3 = 0
        }
        return qw
    }
    /** Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with zeros. */
    public infix fun ushr(bitCount: Int): QuadWord {
        val qw = this.copy()
        if (bitCount >= 64) {
            return QuadWord(0, 0, 0, 0)
        }
        if (bitCount and 0b1 == 1) {
            val lsb1 = (qw.int1 and 0x1) shl 31
            val lsb2 = (qw.int2 and 0x1) shl 31
            val lsb3 = (qw.int3 and 0x1) shl 31
            qw.int0 = (qw.int0 ushr 1) and lsb1
            qw.int1 = (qw.int1 ushr 1) and lsb2
            qw.int2 = (qw.int2 ushr 1) and lsb3
            qw.int3 = (qw.int2 ushr 1)
        }
        if (bitCount % 2 == 0) {
            val lsb1 = (qw.int1 and 0b11) shl 30
            val lsb2 = (qw.int2 and 0b11) shl 30
            val lsb3 = (qw.int3 and 0b11) shl 30
            qw.int0 = (qw.int0 ushr 2) and lsb1
            qw.int1 = (qw.int1 ushr 2) and lsb2
            qw.int2 = (qw.int2 ushr 2) and lsb3
            qw.int3 = (qw.int2 ushr 2)
        }
        if (bitCount % 4 == 0) {
            val lsb1 = (qw.int1 and 0b1111) shl 28
            val lsb2 = (qw.int2 and 0b1111) shl 28
            val lsb3 = (qw.int3 and 0b1111) shl 28
            qw.int0 = (qw.int0 ushr 4) and lsb1
            qw.int1 = (qw.int1 ushr 4) and lsb2
            qw.int2 = (qw.int2 ushr 4) and lsb3
            qw.int3 = (qw.int2 ushr 4)
        }
        if (bitCount % 8 == 0) {
            val lsb1 = (qw.int1 and 0b11111111) shr 24
            val lsb2 = (qw.int2 and 0b11111111) shr 24
            val lsb3 = (qw.int3 and 0b11111111) shr 24
            qw.int0 = (qw.int0 ushr 8) and lsb1
            qw.int1 = (qw.int1 ushr 8) and lsb2
            qw.int2 = (qw.int2 ushr 8) and lsb3
            qw.int3 = (qw.int2 ushr 8)
        }
        if (bitCount % 16 == 0) {
            val lsb1 = (qw.int1 and 0b1111111111111111) shr 16
            val lsb2 = (qw.int2 and 0b1111111111111111) shr 16
            val lsb3 = (qw.int3 and 0b1111111111111111) shr 16
            qw.int0 = (qw.int0 ushr 16) and lsb1
            qw.int1 = (qw.int1 ushr 16) and lsb2
            qw.int2 = (qw.int2 ushr 16) and lsb3
            qw.int3 = (qw.int2 ushr 16)
        }
        if (bitCount % 32 == 0) {
            qw.int0 = qw.int1
            qw.int1 = qw.int2
            qw.int2 = qw.int3
            qw.int3 = Int.MAX_VALUE
        }
        return qw
    }
    /** Performs a bitwise AND operation between the two values. */
    public infix fun and(other: QuadWord): QuadWord {
        val int0 = this.int0 and other.int0
        val int1 = this.int1 and other.int1
        val int2 = this.int2 and other.int2
        val int3 = this.int3 and other.int3
        return QuadWord(int0, int1, int2, int3)
    }
    /** Performs a bitwise OR operation between the two values. */
    public infix fun or(other: QuadWord): QuadWord {
        val int0 = this.int0 or other.int0
        val int1 = this.int1 or other.int1
        val int2 = this.int2 or other.int2
        val int3 = this.int3 or other.int3
        return QuadWord(int0, int1, int2, int3)
    }
    /** Performs a bitwise XOR operation between the two values. */
    public infix fun xor(other: QuadWord): QuadWord {
        val int0 = this.int0 xor other.int0
        val int1 = this.int1 xor other.int1
        val int2 = this.int2 xor other.int2
        val int3 = this.int3 xor other.int3
        return QuadWord(int0, int1, int2, int3)
    }
    /** Inverts the bits in this value. */
    public fun inv(): QuadWord {
        val int0 = this.int0.inv()
        val int1 = this.int1.inv()
        val int2 = this.int2.inv()
        val int3 = this.int3.inv()
        return QuadWord(int0, int1, int2, int3)
    }

    override fun toChar() = int0.toChar()
    override fun toDouble() = this.toLong().toDouble()
    override fun toFloat() = int0.toFloat()
    override fun toInt() = int0
    override fun toLong() = (int1.toLong() shl 32) or int0.toLong()
    override fun toShort() = int0.toShort()
    override fun toByte() = int0.toByte()

    fun low(): Long {
        return this.toLong()
    }

    fun high(): Long {
        return (int3.toLong() shl 32) or int2.toLong()
    }

    public fun copy(): QuadWord {
        return QuadWord(this.int0, this.int1, this.int2, this.int3)
    }

    override fun toString(): String {
        return String.format("0x%016x%016x", high(), low())
    }
}

fun Number.toQuadWord(): QuadWord {
    if (this is QuadWord) {
        return this
    }
    if (this is Int) {
        return this.toInt().toQuadWord()
    }
    if (this is Long) {
        return this.toLong().toQuadWord()
    }
    if (this is Byte) {
        return this.toByte().toQuadWord()
    }
    if (this is Short) {
        return this.toShort().toQuadWord()
    }
    throw TypeCastException("Unknown number type!")
}

fun Byte.toQuadWord(): QuadWord {
    val num = this.toInt()
    return if (num.sign == 1) {
        QuadWord(int0 = num, int1 = Int.MIN_VALUE, int2 = Int.MIN_VALUE, int3 = Int.MIN_VALUE)
    } else {
        QuadWord(int0 = num)
    }
}

fun Short.toQuadWord(): QuadWord {
    val num = this.toInt()
    return if (num.sign == 1) {
        QuadWord(int0 = num, int1 = Int.MIN_VALUE, int2 = Int.MIN_VALUE, int3 = Int.MIN_VALUE)
    } else {
        QuadWord(int0 = num)
    }
}

fun Int.toQuadWord(): QuadWord {
    val num = this
    return if (num.sign == 1) {
        QuadWord(int0 = num, int1 = Int.MIN_VALUE, int2 = Int.MIN_VALUE, int3 = Int.MIN_VALUE)
    } else {
        QuadWord(int0 = num)
    }
}

fun Long.toQuadWord(): QuadWord {
    val num = this
    return if (num.sign == 1) {
        QuadWord(int0 = num.toInt(), int1 = (num shr 32).toInt(), int2 = Int.MIN_VALUE, int3 = Int.MIN_VALUE)
    } else {
        QuadWord(int0 = num.toInt(), int1 = (num shr 32).toInt())
    }
}

fun Byte.toUQuadWord(): QuadWord {
    return QuadWord(int0 = this.toInt())
}

fun Short.toUQuadWord(): QuadWord {
    return QuadWord(int0 = this.toInt())
}

fun Int.toUQuadWord(): QuadWord {
    return QuadWord(int0 = this)
}

fun Long.toUQuadWord(): QuadWord {
    return QuadWord(int0 = this.toInt(), int1 = (this shr 32).toInt())
}

operator fun Int.minus(other: QuadWord): QuadWord {
    return this.toQuadWord() - other
}

// import kotlin.random.Random
//
// class QuadWord private constructor(n: Int) {
//    internal val array:IntArray
//    init{
//        array = IntArray(n shl 2)
//    }
//    fun size():Int {
//        return size(this.array)
//    }
//    /**
//     * Main utility class - points to an index in the array
//     * @param idx
//     * @return
//     */
//    fun newIdx(idx:Int): Idx {
//        return Idx(array).set(idx)
//    }
//    class Idx internal constructor(array: IntArray) {
//        //dont make the field finals
//        internal var idx: Int = 0
//        internal var array: IntArray = array//keep ref. here, reduce the indirection
//
//        fun set(idx:Int): Idx {
//            if (size(array) <= idx || idx < 0)
//                throw IndexOutOfBoundsException((idx).toString())
//            this.idx = idx shl 2
//            return this
//        }
//        fun index(): Int {
//            return idx shr 2
//        }
//        fun shl32(): Idx {
//            val array = this.array
//            var idx = this.idx
//            array[idx] = array[++idx]
//            array[idx] = array[++idx]
//            array[idx] = array[++idx]
//            array[idx] = 0
//            return this
//        }
//        fun shr32(): Idx {
//            val array = this.array
//            var idx = this.idx + 3
//            array[idx] = array[--idx]
//            array[idx] = array[--idx]
//            array[idx] = array[--idx]
//            array[idx] = 0
//            return this
//        }
//        fun or(src: Idx): Idx {
//            val array = this.array
//            var idx = this.idx
//            var idx2 = src.idx
//            val array2 = src.array
//            array[idx++] = array[idx++] or array2[idx2++]
//            array[idx++] = array[idx++] or array2[idx2++]
//            array[idx++] = array[idx++] or array2[idx2++]
//            array[idx++] = array[idx++] or array2[idx2++]
//            return this
//        }
//        fun xor(src: Idx): Idx {
//            val array = this.array
//            var idx = this.idx
//            var idx2 = src.idx
//            val array2 = src.array
//            array[idx++] = array[idx++] xor array2[idx2++]
//            array[idx++] = array[idx++] xor array2[idx2++]
//            array[idx++] = array[idx++] xor array2[idx2++]
//            array[idx++] = array[idx++] xor array2[idx2++]
//            return this
//        }
//        fun add(src: Idx): Idx {
//            val array = this.array
//            var idx = this.idx + 3
//            val array2 = src.array
//            var idx2 = src.idx + 3
//            var l: Long = 0
//            l += array[idx].toLong() and mask
//            l += array2[idx2--].toLong() and mask
//            array[idx--] = (l and mask).toInt()
//            l = l ushr 32
//            l += array[idx].toLong() and mask
//            l += array2[idx2--].toLong() and mask
//            array[idx--] = (l and mask).toInt()
//            l = l ushr 32
//            l += array[idx].toLong() and mask
//            l += array2[idx2--].toLong() and mask
//            array[idx--] = (l and mask).toInt()
//            l = l ushr 32
//            l += array[idx].toLong() and mask
//            l += array2[idx2--].toLong()
//            array[idx] = (l and mask).toInt()
//            // l>>>=32;
//            return this
//        }
//        fun set(high:Long, low:Long): Idx {
//            val array = this.array
//            val idx = this.idx
//            array[idx + 0] = ((high.ushr(32)) and mask).toInt()
//            array[idx + 1] = ((high.ushr(0)) and mask).toInt()
//            array[idx + 2] = ((low.ushr(32)) and mask).toInt()
//            array[idx + 3] = ((low.ushr(0)) and mask).toInt()
//            return this
//        }
//        fun high(): Long {
//            val array = this.array
//            val idx = this.idx
//            val res = ((array[idx].toLong() and mask) shl 32) or (array[idx + 1].toLong() and mask)
//            return res
//        }
//        fun low(): Long {
//            val array = this.array
//            val idx = this.idx
//            val res = ((array[idx + 2].toLong() and mask) shl 32) or (array[idx + 3].toLong() and mask)
//            return res
//        }
//        //ineffective but well
//        public override fun toString():String {
//            return String.format("0x%016x%016x", high(), low())
//        }
//        companion object {
//            private val mask = 0xFFFFFFFFL
//        }
//    }
//    companion object {
//        private fun size(array:IntArray):Int {
//            return array.size shr 2
//        }
//        /**
//         * allocates N 128bit elements. newIdx to create a pointer
//         * @param n
//         * @return
//         */
//        fun allocate(n:Int): QuadWord {
//            return QuadWord(n)
//        }
//        @JvmStatic
//        fun main(args:Array<String>) {
//            val bitz = QuadWord.allocate(256)
//            val idx = bitz.newIdx(0)
//            val idx2 = bitz.newIdx(2)
//            println(idx.set(0, 0xf))
//            println(idx2.set(0, Long.MIN_VALUE).xor(idx))
//            println(idx.set(0, Long.MAX_VALUE).add(idx2.set(0, 1)))
//            println("==")
//            println(idx.add(idx))//can add itself
//            println(idx.shl32())//left
//            println(idx.shr32())//and right
//            println(idx.shl32())//back left
//            //w/ alloc
//            println(idx.add(bitz.newIdx(4).set(0, Long.MAX_VALUE)))
//            //self xor
//            println(idx.xor(idx))
//            //random xor
//            println("===init random===")
//            val r = Random(1112)
//            run({ var i = 0
//                val s = bitz.size()
//                while (i < s) {
//                    idx.set(i).set(r.nextLong(), r.nextLong())
//                    println(idx)
//                    i++
//                }})
//            val theXor = bitz.newIdx(0)
//            var i = 1
//            val s = bitz.size()
//            while (i < s)
//            {
//                theXor.xor(idx.set(i))
//                i++
//            }
//            println("===XOR===")
//            println(theXor)
//        }
//    }
// }