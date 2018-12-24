package venusbackend.riscv.insts.floating

import kotlin.math.abs

class Decimal(f: Float = 0F, d: Double = 0.0, isF: Boolean = true) {
    var float: Float = f
    var double: Double = d
    var isFloat: Boolean = isF
    fun isDouble(): Boolean = !this.isFloat

    fun set(float: Float) {
        this.float = float
        isFloat = true
    }

    fun set(double: Double) {
        this.double = double
        isFloat = false
    }

    fun get(): Number {
        if (this.isFloat) {
            return this.float
        } else {
            return this.double
        }
    }

    fun getCurrentFloat(): Float {
        if (this.isFloat) {
            return this.float
        } else {
            var s = this.double.toRawBits().toString(16)
            s += "0".repeat(16 - s.length)
            return Float.fromBits(s.substring(8 until 16).toInt(16))
        }
    }

    fun getCurrentDouble(): Double {
        if (this.isFloat) {
            var s = this.float.toRawBits().toString(16)
            s += "0".repeat(8 - s.length)
            s = "0".repeat(8) + s
            return Double.fromBits(s.toLong(16))
        } else {
            return this.double
        }
    }

    fun toHex(): String {
        /*FIXME make it convert to hex correctly*/
        var s: String
        if (this.isFloat) {
            var isNeg = false
            val f = if (this.float < 0) {
                isNeg = true
                this.float * -1
            } else {
                this.float
            }
            val b = f.toRawBits()
            s = b.toString(16)
            s = s.removePrefix("-")
            s = "0".repeat(8 - s.length) + s
            if (isNeg) {
                val new: Char = when (s[0]) {
                    '0' -> '8'
                    '1' -> '9'
                    '2' -> 'a'
                    '3' -> 'b'
                    '4' -> 'c'
                    '5' -> 'd'
                    '6' -> 'e'
                    '7' -> 'f'
                    else -> s[0]
                }
                s = new + s.removeRange(0..0)
            }
            s = "0x" + s
        } else {
            var isNeg = false
            val d = if (this.double < 0) {
                isNeg = true
                this.double * -1
            } else {
                this.double
            }
            val b = d.toRawBits()
            s = b.toString(16)
            s = s.removePrefix("-")
            s = "0".repeat(16 - s.length) + s
            if (isNeg) {
                val new: Char = when (s[0]) {
                    '0' -> '8'
                    '1' -> '9'
                    '2' -> 'a'
                    '3' -> 'b'
                    '4' -> 'c'
                    '5' -> 'd'
                    '6' -> 'e'
                    '7' -> 'f'
                    else -> s[0]
                }
                s = new + s.removeRange(0..0)
            }
            s = "0x" + s
        }
        return s
    }

    fun toDecimal(): String {
        var s = ""
        if (this.isFloat) {
            if (this.float.toRawBits() == 0x80000000.toInt()) {
                s = "-"
            }
            s += this.float.toString()
        } else {
            if (this.double.toRawBits().toString(16) == "8000000000000000") {
                s = "-"
            }
            s += this.double.toString()
        }
        return s
    }

    fun toUnsigned(): String {
        val s: String
        if (this.isFloat) {
            s = abs(this.float).toString()
        } else {
            s = abs(this.double).toString()
        }
        return s
    }

    fun toAscii(): String {
        return this.toHex()
    }
}