package venusbackend

operator fun Number.plus(other: Number): Number {
    return ((this.toLong()).plus(other.toLong()))
}

operator fun Number.minus(other: Number): Number {
    return (this.toLong()).minus(other.toLong())
}

operator fun Number.times(other: Number): Number {
    return (this.toLong().times(other.toLong()))
}

operator fun Number.div(other: Number): Number {
    return ((this.toLong()).div(other.toLong()))
}

operator fun Number.inc(): Number {
    return ((this.toLong()).inc())
}

operator fun Number.compareTo(other: Number): Int {
    return ((this.toLong()).compareTo((other.toLong())))
}

infix fun Number.shr(other: Number): Number {
    return ((this.toLong()).shr(other.toInt()))
}

infix fun Number.ushr(other: Number): Number {
    return ((this.toLong()).ushr(other.toInt()))
}

infix fun Number.shl(other: Number): Number {
    return ((this.toLong()).shl(other.toInt()))
}

operator fun Number.rem(other: Number): Number {
    return ((this.toLong()).rem(other.toLong()))
}

infix fun Number.and(other: Number): Number {
    return ((this.toLong()).and((other.toLong())))
}

infix fun Number.or(other: Number): Number {
    return ((this.toLong()).or((other.toLong())))
}