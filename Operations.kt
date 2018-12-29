package venusbackend

import venusbackend.numbers.toQuadWord

operator fun Number.plus(other: Number): Number {
    return ((this.toQuadWord()).plus(other.toQuadWord()))
}

operator fun Number.minus(other: Number): Number {
    return (this.toQuadWord()).minus(other.toQuadWord())
}

operator fun Number.times(other: Number): Number {
    return (this.toQuadWord().times(other.toQuadWord()))
}

operator fun Number.div(other: Number): Number {
    return ((this.toQuadWord()).div(other.toQuadWord()))
}

operator fun Number.inc(): Number {
    return ((this.toQuadWord()).inc())
}

operator fun Number.compareTo(other: Number): Int {
    return ((this.toQuadWord()).compareTo((other.toQuadWord())))
}

infix fun Number.shr(other: Number): Number {
    return ((this.toQuadWord()).shr(other.toInt()))
}

infix fun Number.ushr(other: Number): Number {
    return ((this.toQuadWord()).ushr(other.toInt()))
}

infix fun Number.shl(other: Number): Number {
    return ((this.toQuadWord()).shl(other.toInt()))
}

operator fun Number.rem(other: Number): Number {
    return ((this.toQuadWord()).rem(other.toQuadWord()))
}

infix fun Number.and(other: Number): Number {
    return ((this.toQuadWord()).and((other.toQuadWord())))
}

infix fun Number.or(other: Number): Number {
    return ((this.toQuadWord()).or((other.toQuadWord())))
}