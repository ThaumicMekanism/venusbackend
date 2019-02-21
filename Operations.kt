package venusbackend

import venusbackend.numbers.QuadWord
import venusbackend.numbers.toQuadWord

operator fun Number.plus(other: Number): Number {
    return if ((this is QuadWord) or (other is QuadWord)) {
        ((this.toQuadWord()).plus(other.toQuadWord()))
    } else if ((this is Long) or (other is Long)) {
        ((this.toLong()).plus(other.toLong()))
    } else if ((this is Int) or (other is Int)) {
        ((this.toInt()).plus(other.toInt()))
    } else if ((this is Short) or (other is Short)) {
        ((this.toShort()).plus(other.toShort())) as Number
    } else {
        ((this.toQuadWord()).plus(other.toQuadWord()))
    }
}

operator fun Number.minus(other: Number): Number {
    return if ((this is QuadWord) or (other is QuadWord)) {
        ((this.toQuadWord()).minus(other.toQuadWord()))
    } else if ((this is Long) or (other is Long)) {
        ((this.toLong()).minus(other.toLong()))
    } else if ((this is Int) or (other is Int)) {
        ((this.toInt()).minus(other.toInt()))
    } else if ((this is Short) or (other is Short)) {
        ((this.toShort()).minus(other.toShort())) as Number
    } else {
        ((this.toQuadWord()).minus(other.toQuadWord()))
    }
}

operator fun Number.times(other: Number): Number {
    return if ((this is QuadWord) or (other is QuadWord)) {
        ((this.toQuadWord()).times(other.toQuadWord()))
    } else if ((this is Long) or (other is Long)) {
        ((this.toLong()).times(other.toLong()))
    } else if ((this is Int) or (other is Int)) {
        ((this.toInt()).times(other.toInt()))
    } else if ((this is Short) or (other is Short)) {
        ((this.toShort()).times(other.toShort())) as Number
    } else {
        ((this.toQuadWord()).times(other.toQuadWord()))
    }
}

operator fun Number.div(other: Number): Number {
    return if ((this is QuadWord) or (other is QuadWord)) {
        ((this.toQuadWord()).div(other.toQuadWord()))
    } else if ((this is Long) or (other is Long)) {
        ((this.toLong()).div(other.toLong()))
    } else if ((this is Int) or (other is Int)) {
        ((this.toInt()).div(other.toInt()))
    } else if ((this is Short) or (other is Short)) {
        ((this.toShort()).div(other.toShort())) as Number
    } else {
        ((this.toQuadWord()).div(other.toQuadWord()))
    }
}

operator fun Number.inc(): Number {
    return if ((this is QuadWord)) {
        (this.toQuadWord()).inc()
    } else if ((this is Long)) {
        ((this.toLong()).inc())
    } else if ((this is Int)) {
        ((this.toInt()).inc())
    } else if ((this is Short)) {
        ((this.toShort()).inc()) as Number
    } else {
        ((this.toQuadWord()).inc())
    }
}

operator fun Number.compareTo(other: Number): Int {
    return if ((this is QuadWord)) {
        ((this.toQuadWord()).compareTo(other.toQuadWord()))
    } else if ((this is Long)) {
        ((this.toLong()).compareTo(other.toLong()))
    } else if ((this is Int)) {
        ((this.toInt()).compareTo(other.toInt()))
    } else if ((this is Short)) {
        ((this.toShort()).compareTo(other.toShort()))
    } else {
        ((this.toQuadWord()).compareTo(other.toQuadWord()))
    }
}

infix fun Number.shr(other: Number): Number {
    return if ((this is QuadWord)) {
        (this.toQuadWord()).shr(other.toInt())
    } else if ((this is Long)) {
        ((this.toLong()).shr(other.toInt()))
    } else if ((this is Int)) {
        ((this.toInt()).shr(other.toInt()))
    } else if ((this is Short)) {
        ((this.toInt()).shr(other.toInt())).toShort() as Number
    } else {
        ((this.toQuadWord()).shr(other.toInt()))
    }
}

infix fun Number.ushr(other: Number): Number {
    return if ((this is QuadWord)) {
        (this.toQuadWord()).ushr(other.toInt())
    } else if ((this is Long)) {
        ((this.toLong()).ushr(other.toInt()))
    } else if ((this is Int)) {
        ((this.toInt()).ushr(other.toInt()))
    } else if ((this is Short)) {
        ((this.toInt()).ushr(other.toInt())).toShort() as Number
    } else {
        ((this.toQuadWord()).ushr(other.toInt()))
    }
}

infix fun Number.shl(other: Number): Number {
    return if ((this is QuadWord)) {
        (this.toQuadWord()).shl(other.toInt())
    } else if ((this is Long)) {
        ((this.toLong()).shl(other.toInt()))
    } else if ((this is Int)) {
        ((this.toInt()).shl(other.toInt()))
    } else if ((this is Short)) {
        ((this.toInt()).shl(other.toInt())).toShort() as Number
    } else {
        ((this.toQuadWord()).shl(other.toInt()))
    }
}

operator fun Number.rem(other: Number): Number {
    return if ((this is QuadWord) or (other is QuadWord)) {
        ((this.toQuadWord()).rem(other.toQuadWord()))
    } else if ((this is Long) or (other is Long)) {
        ((this.toLong()).rem(other.toLong()))
    } else if ((this is Int) or (other is Int)) {
        ((this.toInt()).rem(other.toInt()))
    } else if ((this is Short) or (other is Short)) {
        ((this.toShort()).rem(other.toShort())) as Number
    } else {
        ((this.toQuadWord()).rem(other.toQuadWord()))
    }
}

infix fun Number.and(other: Number): Number {
    return if ((this is QuadWord) or (other is QuadWord)) {
        ((this.toQuadWord()).and(other.toQuadWord()))
    } else if ((this is Long) or (other is Long)) {
        ((this.toLong()).and(other.toLong()))
    } else if ((this is Int) or (other is Int)) {
        ((this.toInt()).and(other.toInt()))
    } else if ((this is Short) or (other is Short)) {
        ((this.toShort()).and(other.toShort())) as Number
    } else {
        ((this.toQuadWord()).and(other.toQuadWord()))
    }
}

infix fun Number.or(other: Number): Number {
    return if ((this is QuadWord) or (other is QuadWord)) {
        ((this.toQuadWord()).or(other.toQuadWord()))
    } else if ((this is Long) or (other is Long)) {
        ((this.toLong()).or(other.toLong()))
    } else if ((this is Int) or (other is Int)) {
        ((this.toInt()).or(other.toInt()))
    } else if ((this is Short) or (other is Short)) {
        ((this.toInt()).or(other.toInt())).toShort() as Number
    } else {
        ((this.toQuadWord()).or(other.toQuadWord()))
    }
}