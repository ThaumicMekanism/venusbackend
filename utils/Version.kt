package venusbackend.utils

import kotlin.math.max

class Version(version: String) : Comparable<Version> {
    private val version: String
    fun get(): String {
        return version
    }

    override operator fun compareTo(that: Version): Int {
        if (that == null) return 1
        val thisParts = this.get().split(Regex("\\.")).toTypedArray()
        val thatParts = that.get().split(Regex("\\.")).toTypedArray()
        val length: Int = max(thisParts.size, thatParts.size)
        for (i in 0 until length) {
            val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
            val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
            if (thisPart < thatPart) return -1
            if (thisPart > thatPart) return 1
        }
        return 0
    }

    override fun equals(that: Any?): Boolean {
        if (this === that) return true
        if (that == null) return false
        return if (this::class != that::class) false else this.compareTo(that as Version) == 0
    }

    init {
        require(version.matches(Regex("[0-9]+(\\.[0-9]+)*"))) { "Invalid version format" }
        this.version = version
    }
}