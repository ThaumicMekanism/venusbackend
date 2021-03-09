package venusbackend.simulator
import venusbackend.and

class WatchPoint(
    var onRead: Boolean = false,
    var onWrite: Boolean = false,
    var address: Number? = null,
    var value: Number? = null,
    var mask: Number? = null
) {

    companion object {
        fun parse(string: String): WatchPoint {
            val split = string.split("|")
            if (split.size != 5) {
                throw SimulatorError("String is not a valid watch point! $string")
            }
            val addr = if (split[2] == "None") {
                null
            } else {
                split[2].toLong()
            }
            val va = if (split[3] == "None") {
                null
            } else {
                split[3].toLong()
            }
            val msk = if (split[4] == "None") {
                null
            } else {
                split[4].toLong()
            }
            return WatchPoint(
                    split[0].toBoolean(),
                    split[1].toBoolean(),
                    addr,
                    va,
                    msk
            )
        }
    }

    fun stringify(): String {
        val addr = address?.toString() ?: "None"
        val va = value?.toString() ?: "None"
        val msk = mask?.toString() ?: "None"
        return "$onRead|$onWrite|$addr|$va|$msk"
    }

    init {
        if (!onRead && !onWrite) {
            throw SimulatorError("Watch points must be looking for a read OR write!")
        }
        if (address == null && value == null) {
            throw SimulatorError("Watch points must have either a value to look for or an address!")
        }
        if (mask != null && address == null) {
            throw SimulatorError("Watch points which have a mask must set an address!")
        }
    }

    private fun maskedAddress(addr: Number?): Number? {
        return if (this.mask == null || addr == null) {
            addr
        } else {
            addr and this.mask!!
        }
    }

    // isStore is true for a store, false for a load
    fun eval(isStore: Boolean, address: Number, value: Number): Boolean {
        return (
                (onWrite && isStore) ||
                (onRead && !isStore)
                ) && (
                    (this.address == null || (maskedAddress(this.address)?.toLong() == maskedAddress(address)?.toLong())) &&
                    (this.value == null || (this.value?.toLong() == value.toLong()))
                )
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null || other !is WatchPoint) {
            return false
        }
        val wp = other as WatchPoint
        return this.onRead == wp.onRead &&
                this.onWrite == wp.onWrite &&
                this.address == wp.address &&
                this.value == wp.value &&
                this.mask == wp.mask
    }

    override fun hashCode(): Int {
        return this.stringify().hashCode()
    }
}
