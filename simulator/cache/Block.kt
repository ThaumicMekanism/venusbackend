package venusbackend.simulator.cache

import venus.Renderer

/**
 * Created by thaum on 7/29/2018.
 */
/**
 * A collection of adjacent word entries within the cHandler. The number of entries
 * is determined by blocksize.
 *
 * @author Deborah Hawkins
 * @author Rishi Dewan
 */
class Block
/**
 * Class constructor specifing size of block to create in bytes. All blocks
 * are created with this constructor and then modified.
 */
(private val blocksize: Int) {

    public var blockState = BlockState.EMPTY
    /**
     * Returns the tag field of the address. The containing set uses this to
     * find an exact address.
     */
    var tag: Int = 0
        private set
    /**
     * Returns true if the data contained in this block is valid or false if it
     * has not been set yet.
     */
    var isValid: Boolean = false
        private set
    /**
     * Returns true if the data contained in this block has been updated or
     * false if it is still consistent with data in main memory.
     */
    var isDirty: Boolean = false
        private set
    /**
     * Returns an int which indicates when the block was last used. Higher
     * numbers indicate more recent use.
     */
    var recentUse: Int = 0
        private set // higher values are more recently used
    // private var words: IntArray? = null

    init {
        isValid = false // doesn't contain valid data yet
        isDirty = false // doesn't need to be written back
        recentUse = 0 // indicates not used yet
    }

    /**
     * Returns the word (32 bytes) of data associated with a particular word
     * address. Both data and address are expressed as (base 10) ints at this
     * level.
     *
     * @param address the 32-bit address (as an int) at which to enter the data
     * @param recentUse an update to the recentUse field
     * @pre Check that this block is valid and that the address exists
     * in this block before calling this function.
     */
    fun read(address: Int, useCounter: Int) {
        recentUse = useCounter
        // return words!![address % blocksize]
    }

    /**
     * Replaces a word of data within the block for the byte address
     * specified.
     *
     * @param address the 32-bit address (as an int) at which to enter the data
     * @param data a 32-bit datum expressed as an int
     * @param recentUse an update to the recentUse field
     * @throws NotInCacheException If the address specified does not exist
     * within this block or if the block is invalid.
     */
    fun write(offset: Int, /*data: Int,*/ useCounter: Int) {
        recentUse = useCounter
        // words!![offset] = data
        isValid = true
        isDirty = true
    }

    /**
     * Fills the block with new data. Should only be used when the block is not
     * dirty and is about to be used for a read or write.
     *
     * @param defines the block uniquely within this set (the first part of the address)
     * @param words an array of data which represent the words in this block
     */
    fun writeBlock(tag: Int/*, words: IntArray*/) {
        this.tag = tag
        // this.words = words
        isValid = true
        isDirty = false
    }

    /**
     * Returns the array of all words in the block. Used when the block is dirty
     * and in the least recently used block, so the data can be written back to
     * memory. This allocates new space for other data.
     */
    /*fun readAllData(): IntArray? {
        return words
    }*/

    fun copy(): Block {
        val b = Block(blocksize)
        b.tag = this.tag
        b.isValid = this.isValid
        b.isDirty = this.isDirty
        b.recentUse = this.recentUse
        b.blockState = this.blockState
        // b.words = this.words?.copyOf()
        return b
    }

    /**
     * Prints the block values to standard out.
     */
    override fun toString(): String {
        var result = ((if (isValid) 1 else 0).toString() + "        " + Renderer.toHex(tag) + "   " +
                (if (isDirty) 1 else 0) + "        ")
//        for (word in words!!)
//            result += Renderer.toHex(word) + "     "
        return result
    }
}