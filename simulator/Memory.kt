package venusbackend.simulator

import venusbackend.plus
import venusbackend.shr

/**
 * A class representing a computer's memory.
 */
class Memory {
    /**
     * A hashmap which maps addresses to the value stored at that place in memory.
     *
     * Unlike MARS, I've made the design decision to use a hashmap. This allows for us to write anywhere in memory
     * without being concerned with writing out of bounds (4 MB). The downside is that this has a higher overhead.
     *
     * @todo Transition to a `HashMap<Int, Int>`, which will have a smaller overhead (although more code complexity)
     */
    // TODO Change this from long :(
    private val memory = HashMap<Long, Byte>()

    fun removeByte(addr: Number) {
        memory.remove(addr)
    }

    /**
     * Loads an unsigned byte from memory
     *
     * @param addr the address to load from
     * @return the byte at that location, or 0 if that location has not been written to
     */
//    fun loadByte(addr: Number): Int = memory[addr]?.toInt()?.and(0xff) ?: 0
    fun loadByte(addr: Number): Int {
        val v = memory[addr.toLong()]
        return v?.toInt()?.and(0xff) ?: 0
    }

    /**
     * Loads an unsigned halfword from memory
     *
     * @param addr the address to load from
     * @return the halfword at that location, or 0 if that location has not been written to
     */
//    fun loadHalfWord(addr: Number): Int = (loadByte(addr + 1) shl 8) or loadByte(addr)
    fun loadHalfWord(addr: Number): Int {
        val lsb = loadByte(addr)
        val msbb = loadByte(addr + 1)
        val msb = (msbb shl 8)
        return msb or lsb
    }

    /**
     * Loads a word from memory
     *
     * @param addr the address to load from
     * @return the word at that location, or 0 if that location has not been written to
     */
    fun loadWord(addr: Number): Int = (loadHalfWord(addr + 2) shl 16) or loadHalfWord(addr)

    /**
     * Loads a long from memory
     *
     * @param addr the address to load from
     * @return the long at that location, or 0 if that location has not been written to
     */
    fun loadLong(addr: Number): Long = (loadWord(addr + 4).toLong() shl 32) or loadWord(addr).toLong()

    /**
     * Stores a byte in memory, truncating the given Int if necessary
     *
     * @param addr the address to write to
     * @param value the value to write
     */
    fun storeByte(addr: Number, value: Number) { memory[addr.toLong()] = value.toByte() }

    /**
     * Stores a halfword in memory, truncating the given Int if necessary
     *
     * @param addr the address to write to
     * @param value the value to write
     */
    fun storeHalfWord(addr: Number, value: Number) {
        storeByte(addr, value)
        storeByte(addr + 1, value shr 8)
    }

    /**
     * Stores a word in memory
     *
     * @param addr the address to write to
     * @param value the value to write
     */
    fun storeWord(addr: Number, value: Number) {
        storeHalfWord(addr, value)
        storeHalfWord(addr + 2, value shr 16)
    }

    /**
     * Stores a word in memory
     *
     * @param addr the address to write to
     * @param value the value to write
     */
    fun storeLong(addr: Number, value: Number) {
        storeWord(addr, value)
        storeWord(addr + 4, value shr 32)
    }
}