package venusbackend.simulator.cache

import venusbackend.riscv.Address

class Cache
/* ktlint-disable no-multi-spaces */
/**
 *
 * @param capacity      the size in words of the cHandler
 * @param associativity the number of blocks in each set
 * @param blocksize     the size in bytes of each block
 * @param memory        a reference to main memory
 */
/* ktlint-enable no-multi-spaces */
(c: CacheHandler) {
    internal var numWrites: Int = 0
    internal var numReads: Int = 0
    internal var numWriteMisses: Int = 0
    internal var numReadMisses: Int = 0
    private var numEvictions: Int = 0
    private val c = c
    internal var sets: Array<Set>

    init {

        // initialize counters
        numWrites = 0
        numReads = 0
        numWriteMisses = 0
        numReadMisses = 0
        numEvictions = 0

        // initialize values of the cHandler
        // val s = arrayOfNulls<Set>(capacity / (associativity * blocksize))
        val s = arrayOfNulls<Set>(c.cacheSize() / (c.associativity() * c.cacheBlockSize()))
        // sets = new Set[ this.capacity / (this.associativity * this.blocksize) ];

        // create the sets that make up this cHandler
        for (i in s.indices)
            // s[i] = Set(this.associativity, this.blocksize)
            s[i] = Set(c.associativity(), c.cacheBlockSize(), c)
        sets = s.filterNotNull().toTypedArray()
    }

    fun read(address: Address): Boolean {

        // if the block isn't found in the cHandler, put it there
        var h = true
        var bs = BlockState.HIT
        if (!isInMemory(address)) {
            c.nextLevelCacheHandler?.read(address)
            allocate(address.address)
            numReadMisses++
            h = false
            bs = BlockState.MISS
        }

        // calculate what set the block is in and ask it for the data
        val set = sets[address.address / this.c.cacheBlockSize() % sets.size]
        val blockoffset = address.address % this.c.cacheBlockSize()
        numReads++
        set.read(getTag(address.address), blockoffset, bs)
        return h
    }

    fun write(address: Address/*, data: Int*/): Boolean {

        // if the block isn't found in the cHandler, put it there
        var h = true
        var bs = BlockState.HIT
        if (!isInMemory(address)) {
            c.nextLevelCacheHandler?.write(address)
            allocate(address.address)
            numWriteMisses++
            h = false
            bs = BlockState.MISS
        }

        // calculate what set the block is in and ask it for the data
        val index = address.address / this.c.cacheBlockSize() % sets.size
        val blockoffset = address.address % this.c.cacheBlockSize()
        val set = sets[index]
        numWrites++
        set.write(getTag(address.address), blockoffset/*, data*/, bs)
        return h
    }

    private fun isInMemory(address: Address): Boolean {
        val index = address.address / this.c.cacheBlockSize() % sets.size
        return sets[index].findBlock(getTag(address.address)) != null
    }

    private fun getTag(address: Int): Int {
        // System.out.println((int) ( (double)address / ((double)sets.length * (double)blocksize)));
        return address / (sets.size * this.c.cacheBlockSize())
    }

    // should only be called if we already know the address is not in cache
    private fun allocate(address: Int) {

        // val index = address / blocksize % sets.size
        val index = address / c.cacheBlockSize() % sets.size
        val set = sets[index]
        var evictee: Block
        if (c.blockRepPolicy().equals(BlockReplacementPolicy.RANDOM)) {
            evictee = set.getRandom()
        } else {
            evictee = set.getLRU()
        }

        // If the least recently used block is dirty, write it back to
        // main memory.
        if (evictee.isDirty) {
            numEvictions++
            // val evicteeAddress = (evictee.getTag() * sets.size + index) * blocksize
            val evicteeAddress = (evictee.tag * sets.size + index) * c.cacheBlockSize()
        }

        // Then write the block with the data we need
        // val SAddress = address / blocksize * blocksize
        val SAddress = (address / c.cacheBlockSize()) * c.cacheBlockSize()
        evictee.writeBlock(getTag(address))
    }

    fun copy(): Cache {
        val cc = Cache(this.c)
        cc.numEvictions = this.numEvictions
        cc.numReadMisses = this.numReadMisses
        cc.numReads = this.numReads
        cc.numWriteMisses = this.numWrites
        cc.numWrites = this.numWrites
        for (s in this.sets.indices) {
            cc.sets[s] = this.sets[s].copy()
        }
        return cc
    }

    fun blockStates(): ArrayList<String> {
        val bss = ArrayList<String>()
        for (s in sets) {
            s.blockStates(bss)
        }
        return bss
    }
}