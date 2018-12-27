package venusbackend.simulator

import java.math.BigInteger
import kotlin.experimental.and

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
     * @todo Change address from int to something else to support larger address spaces. Maybe switch to bigint for the address.
     */
    private val memory = HashMap<Number, Byte>()

    fun removeByte(addr: Short) {
        memory.remove(addr)
    }
    fun removeByte(addr: Int) {
        memory.remove(addr)
    }
    fun removeByte(addr: Long) {
        memory.remove(addr)
    }
    fun removeByte(addr: BigInteger) {
        memory.remove(addr)
    }

    /**
     * Loads an unsigned byte from memory
     *
     * @param addr the address to load from
     * @return the byte at that location, or 0 if that location has not been written to
     */
    fun loadByte(addr: Short): Short = memory[addr]?.toShort()?.and(0xff) ?: 0.toShort()
    fun loadByte(addr: Int): Int = memory[addr]?.toInt()?.and(0xff) ?: 0
    fun loadByte(addr: Long): Long = memory[addr]?.toLong()?.and(0xff) ?: 0.toLong()
    // Need to convert to int before bigint since cannot go directly from byte to bigint.
    fun loadByte(addr: BigInteger): BigInteger = memory[addr]?.toInt()?.toBigInteger()?.and(0xff.toBigInteger()) ?: 0.toBigInteger()

    /**
     * Loads an unsigned halfword from memory
     *
     * @param addr the address to load from
     * @return the halfword at that location, or 0 if that location has not been written to
     */
    fun loadHalfWord(addr: Short): Short = ((loadByte(addr + 1) shl 8) or loadByte(addr).toInt()).toShort()
    fun loadHalfWord(addr: Int): Int = (loadByte(addr + 1) shl 8) or loadByte(addr)
    fun loadHalfWord(addr: Long): Long = (loadByte(addr + 1) shl 8) or loadByte(addr)
    fun loadHalfWord(addr: BigInteger): BigInteger = (loadByte(addr + 1.toBigInteger()) shl 8) or loadByte(addr)

    /**
     * Loads a word from memory
     *
     * @param addr the address to load from
     * @return the word at that location, or 0 if that location has not been written to
     */
    fun loadWord(addr: Int): Int = (loadHalfWord(addr + 2) shl 16) or loadHalfWord(addr)
    fun loadWord(addr: Long): Long = (loadHalfWord(addr + 2) shl 16) or loadHalfWord(addr)
    fun loadWord(addr: BigInteger): BigInteger = (loadHalfWord(addr + 2.toBigInteger()) shl 16) or loadHalfWord(addr)

    /**
     * Loads a long from memory
     *
     * @param addr the address to load from
     * @return the long at that location, or 0 if that location has not been written to
     */
    fun loadLong(addr: Int): Long = (loadWord(addr + 4).toLong() shl 32) or loadWord(addr).toLong()
    fun loadLong(addr: BigInteger): Long = (loadWord(addr + 4.toBigInteger()).toLong() shl 32) or loadWord(addr).toLong()

    /**
     * Stores a byte in memory, truncating the given Int if necessary
     *
     * @param addr the address to write to
     * @param value the value to write
     */
    fun storeByte(addr: Short, value: Short) { memory[addr] = value.toByte() }
    fun storeByte(addr: Int, value: Int) { memory[addr] = value.toByte() }
    fun storeByte(addr: Long, value: Long) { memory[addr] = value.toByte() }
    fun storeByte(addr: BigInteger, value: BigInteger) { memory[addr] = value.toByte() }

    /**
     * Stores a halfword in memory, truncating the given Int if necessary
     *
     * @param addr the address to write to
     * @param value the value to write
     */
    fun storeHalfWord(addr: Short, value: Short) {
        storeByte(addr, value)
        storeByte((addr + 1).toShort(), (value.toInt() shr 8).toShort())
    }
    fun storeHalfWord(addr: Int, value: Int) {
        storeByte(addr, value)
        storeByte(addr + 1, value shr 8)
    }
    fun storeHalfWord(addr: Long, value: Long) {
        storeByte(addr, value)
        storeByte(addr + 1, value shr 8)
    }
    fun storeHalfWord(addr: BigInteger, value: BigInteger) {
        storeByte(addr, value)
        storeByte(addr + 1.toBigInteger(), value shr 8)
    }

    /**
     * Stores a word in memory
     *
     * @param addr the address to write to
     * @param value the value to write
     */
    fun storeWord(addr: Int, value: Int) {
        storeHalfWord(addr, value)
        storeHalfWord(addr + 2, value shr 16)
    }
    fun storeWord(addr: Long, value: Long) {
        storeHalfWord(addr, value)
        storeHalfWord(addr + 2, value shr 16)
    }
    fun storeWord(addr: BigInteger, value: BigInteger) {
        storeHalfWord(addr, value)
        storeHalfWord(addr + 2.toBigInteger(), value shr 16)
    }

    /**
     * Stores a word in memory
     *
     * @param addr the address to write to
     * @param value the value to write
     */
    fun storeLong(addr: Long, value: Long) {
        storeWord(addr, value)
        storeWord(addr + 4, value shr 32)
    }
    fun storeLong(addr: BigInteger, value: BigInteger) {
        storeWord(addr, value)
        storeWord(addr + 4.toBigInteger(), value shr 32)
    }
}