package venusbackend.riscv.insts.integer.base.i.ecall

import venus.Renderer
import venusbackend.simulator.Simulator
import venusbackend.toHex

/**
 * This is a wrapper file for all basic malloc operations.
Malloc Node has the format:
    char*[0] where this is of size size.
    uppermagic
    prevNode
    nextNode
    free
    size
    lowermagic
nodeAddr^
*/

data class MallocNode(
    var size: Int,
    var free: Int,
    var nextNode: Int,
    var prevNode: Int,
    var nodeAddr: Int,
    var aupperMagic: Int = upperMagic,
    var alowerMagic: Int = lowerMagic
) {
    companion object {
        val lowBuffer = 0
        val highBuffer = 0
        val sizeof = 4 * 6 + lowBuffer + highBuffer
        val upperMagic: Int = 0x3CBCBCBC
        val lowerMagic: Int = 0x3CDCDCDC
        val minSize: Int = 1
        val nodes: HashMap<Int, MallocNode> = HashMap()
        fun loadBlock(sim: Simulator, nodeAddr: Int, ignore_magic: Boolean = false): MallocNode? {
            if (nodeAddr == 0) {
                return null
            }
            val lM = sim.loadWordwCache(nodeAddr + lowBuffer)
            val size = sim.loadWordwCache(nodeAddr + lowBuffer + 4)
            val free = sim.loadWordwCache(nodeAddr + lowBuffer + 8)
            val nextNode = sim.loadWordwCache(nodeAddr + lowBuffer + 12)
            val prevNode = sim.loadWordwCache(nodeAddr + lowBuffer + 16)
            val uM = sim.loadWordwCache(nodeAddr + lowBuffer + 20)
            val node = MallocNode(
                    size = size,
                    free = free,
                    nextNode = nextNode,
                    prevNode = prevNode,
                    nodeAddr = nodeAddr,
                    alowerMagic = lM,
                    aupperMagic = uM
            )

            if (((uM != upperMagic) || (lM != lowerMagic)) && !ignore_magic) {
                Renderer.stderr("The magic value for this malloc node is incorrect! This means you are overriding malloc metadata OR have specified the address of an incorrect malloc node!\n")
                Renderer.stderr(node)
                return null
            }
            return node
        }
    }

    override fun toString(): String {
        return "Node Address: ${toHex(nodeAddr)}\nsize: ${toHex(size)} B\nfree: ${isFree()}\nPrevious Node Address: ${toHex(prevNode)}\nNext Node Address: ${toHex(nextNode)}\nLower Magic Value: A: ${toHex(alowerMagic)} E: ${toHex(lowerMagic)}\nUpper Magic Value: A: ${toHex(aupperMagic)} E: ${toHex(upperMagic)}\nMetadata Size: ${toHex(sizeof)} B\nMinimum Node Size (Bytes): ${toHex(minSize)} B\nLower Buffer Size: ${toHex(lowBuffer)} B\nUpper Buffer Size: ${toHex(highBuffer)} B\nSentinel: ${isSentinel()}\nNull (not in memory): ${isNull()}"
    }

    fun storeMagic(sim: Simulator) {
        nodes[this.nodeAddr] = this
        sim.storeWordwCache(this.nodeAddr, lowerMagic)
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 20, upperMagic)
    }

    fun storeSize(sim: Simulator) {
        nodes[this.nodeAddr] = this
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 4, this.size)
    }

    fun storeFree(sim: Simulator) {
        nodes[this.nodeAddr] = this
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 8, this.free)
    }

    fun storeNextNode(sim: Simulator) {
        nodes[this.nodeAddr] = this
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 12, this.nextNode)
    }

    fun storePrevNode(sim: Simulator) {
        nodes[this.nodeAddr] = this
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 16, this.prevNode)
    }

    fun storeNode(sim: Simulator) {
        if (this.nodeAddr == 0) {
            Renderer.stderr("Prevented a store of a null malloc node!\n")
            return
        }
        this.storeMagic(sim)
        this.storeSize(sim)
        this.storeFree(sim)
        this.storeNextNode(sim)
        this.storePrevNode(sim)
    }

    fun isNextNull(): Boolean {
        return this.nextNode == 0
    }

    fun isPrevNull(): Boolean {
        return this.prevNode == 0
    }

    fun isFree(): Boolean {
        return this.free != 0
    }

    fun isNull(): Boolean {
        return this.nodeAddr == 0
    }

    fun isSentinel(): Boolean {
        return this.size == 0 && !this.isFree() && this.isPrevNull()
    }

    fun canFit(size: Int): Boolean {
        return this.isFree() && (this.size >= size)
    }

    fun dataAddr(): Int {
        return this.nodeAddr + sizeof
    }

    fun getNextNode(sim: Simulator): MallocNode? {
        return if (this.nextNode == 0) {
            MallocNode(0, 1, 0, 0, 0)
        } else {
            loadBlock(sim, this.nextNode)
        }
    }

    fun getPrevNode(sim: Simulator): MallocNode? {
        return loadBlock(sim, this.prevNode)
    }

    fun allocateNode(sim: Simulator, wantedSize: Int, calloc: Boolean = false): Boolean? {
        if (this.size < wantedSize) {
            return false
        }
        this.free = 0
        if (calloc) {
            if (sim.memset(this.dataAddr(), 0, wantedSize) == 0) {
                this.storeNode(sim)
                return false
            }
        }
        if (this.size > wantedSize + MallocNode.sizeof + MallocNode.minSize) {
            val newNodeAddr = this.dataAddr() + wantedSize
            val newNodeSize = this.size - (wantedSize + MallocNode.sizeof)
            val newNode = MallocNode(newNodeSize, 1, this.nextNode, this.nodeAddr, newNodeAddr)
            if (this.nextNode != 0) {
                val nextNodeMetadata = getNextNode(sim) ?: return null
                nextNodeMetadata.prevNode = newNodeAddr
                nextNodeMetadata.storeNode(sim)
            }
            this.nextNode = newNodeAddr
            this.size = wantedSize
            newNode.storeNode(sim)
        }
        this.storeNode(sim)
        return true
    }

    fun freeNode(sim: Simulator) {
        if (this.isFree()) {
            Renderer.stderr("Double free!\n")
            Renderer.stderr(this)
            return
        }
        if (this.isSentinel()) {
            Renderer.stderr("You cannot free the sentinel node!\n")
            Renderer.stderr(this)
            return
        }
        this.free = 1
        this.storeFree(sim)
        return
        // There is a bug in the below code which is causing incorrect behavior when allocating 5 items then
        // deallocating them then reallocating larger versions of them.
        var next: MallocNode = this
        var prev: MallocNode = this
        var s = 0

        while (next.getNextNode(sim)?.isFree() ?: return) {
            next = next.getNextNode(sim) ?: return
            if (next.isNull()) {
                break
            }
            s += MallocNode.sizeof + next.size
        }

        while (!prev.isSentinel() && prev.getPrevNode(sim)?.isFree() ?: return) {
            prev = prev.getPrevNode(sim) ?: return
            s += MallocNode.sizeof + prev.size
        }

        if (this != next || this != prev) {
            if (this != prev) {
                s += MallocNode.sizeof + this.size
            }
            prev.size += s - prev.size

            prev.nextNode = next.nodeAddr
            next.prevNode = prev.nodeAddr
            if (!next.isNull()) {
                next.storeNode(sim)
            }
            prev.storeNode(sim)
        }

//        while (!next.isNull()) {
//            next = next.getNextNode(sim)!!
//            if (!next.isFree()) {
//                break
//            }
//            s += MallocNode.sizeof + next.size
//        }
//        var prev = this
//        while (!prev.isPrevNull() && prev.getPrevNode(sim)?.isFree() == true && prev.getPrevNode(sim)?.isPrevNull() != true) {
//            if (!prev.isFree()) {
//                break
//            }
//            prev = prev.getPrevNode(sim)!!
//            s += MallocNode.sizeof + prev.size
//        }
//        if (this != next || this != prev) {
//            if (this != prev) {
//                s += MallocNode.sizeof + this.size
//            }
//            prev.size += s
//
//            prev.nextNode = next.nodeAddr
//            next.prevNode = prev.nodeAddr
//            next.storeNode(sim)
//            prev.storeNode(sim)
//        }
    }
}

class Alloc(val sim: Simulator) {
    var initialized = false
    var sentinelMetadata: Int = 0

    fun initialize() {
        sentinelMetadata = sim.getHeapEnd().toInt()
        val sentinel = MallocNode(0, 0, 0, 0, sentinelMetadata)
        sim.addHeapSpace(MallocNode.sizeof + sentinel.size)
        sentinel.storeNode(sim)
        initialized = true
    }

    companion object {
        fun getMetadata(ptr: Int): Int {
            val loc = ptr - MallocNode.sizeof
            return if (loc < 0) {
                0
            } else {
                loc
            }
        }
    }
    var alwaysCalloc = false

    fun malloc(size: Int, calloc: Boolean = alwaysCalloc): Int {
        if (!initialized) {
            initialize()
        }
        if (size <= 0) {
            return 0
        }
        var m = MallocNode.loadBlock(sim, sentinelMetadata) ?: run {
            Renderer.stderr("Failed to get the sentinel metadata block!\n")
            Renderer.stderr(MallocNode.loadBlock(sim, sentinelMetadata, true) ?: "null")
            return 0
        }

        while (!m.isNextNull()) {
            m = m.getNextNode(sim) ?: run {
                return 0
            }
            if (m.canFit(size)) {
                if (m.allocateNode(sim, size, calloc) ?: return 0) {
                    return m.dataAddr()
                }
            }
        }
        val sizeToAdd = size + MallocNode.sizeof
        // TODO DO rlim check Currently there is a janky check
        if (sim.willHeapOverrideStack(sizeToAdd)) {
            return 0
        }

        val newMallocNode = MallocNode(size, 0, 0, m.nodeAddr, sim.getHeapEnd().toInt())
        sim.addHeapSpace(sizeToAdd)
        m.nextNode = newMallocNode.nodeAddr
        m.storeNextNode(sim)
        newMallocNode.storeNode(sim)
        return newMallocNode.dataAddr()
    }

    fun calloc(nitems: Int, size: Int): Int {
        return this.malloc(nitems * size, calloc = true)
    }

    fun realloc(ptr: Int, size: Int): Int {
        if (size == 0) {
            return if (ptr == 0) {
                0
            } else {
                this.free(ptr)
                0
            }
        }
        val tm: MallocNode? = MallocNode.loadBlock(sim, getMetadata(ptr))
        if ((ptr == 0) || (tm == null)) {
            return this.malloc(size)
        }
        var m: MallocNode = tm
        val newBlock: Int = malloc(size)
        if (newBlock == 0) {
            return 0
        }
        sim.memcpy(newBlock, m.dataAddr(), size)
        m.freeNode(sim)
        return newBlock
    }

    fun free(ptr: Int) {
        if (ptr != 0) {
            MallocNode.loadBlock(sim, getMetadata(ptr))?.freeNode(sim)
        }
    }

    /**
     * Counts the number of blocks which are not free.
     * Returns -1 if an error has occurred (aka a corrupted list)
     */
    fun numActiveBlocks(): Int {
        if (!initialized) {
            return 0
        }
        var counter = 0
        var m = MallocNode.loadBlock(sim, sentinelMetadata) ?: run {
            Renderer.stderr("Failed to get the sentinel metadata block!\n")
            Renderer.stderr(MallocNode.loadBlock(sim, sentinelMetadata, true) ?: "null")
            return -1
        }
        if (m.isNextNull()) {
            // We do not want to count the sentinel block!
            return counter
        }
        while (!m.isNextNull()) {
            // If we fail to get the next node when it should exist, we must return -1 to indicate an error.
            m = m.getNextNode(this.sim) ?: return -1
            if (!m.isFree()) {
                if (m.size > 0) { // We want to ignore the sentinel node.
                    counter++
                }
            }
        }
        return counter
    }
}