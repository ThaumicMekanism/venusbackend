package venusbackend.riscv.insts.integer.base.i.ecall

import venusbackend.simulator.Simulator

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
        var nodeAddr: Int
) {
    companion object {
        val lowBuffer = 0
        val highBuffer = 0
        val sizeof = 4 * 6 + lowBuffer + highBuffer
        val upperMagic: Int = 0x3CBCBCBC
        val lowerMagic: Int = 0x3CDCDCDC
        val minSize: Int = 1
        fun loadBlock(sim: Simulator, nodeAddr: Int): MallocNode? {
            if (nodeAddr == 0) {
                return null
            }
            val lM = sim.loadWordwCache(nodeAddr + lowBuffer)
            val uM = sim.loadWordwCache(nodeAddr + lowBuffer + 20)
            if ((uM != upperMagic) || (lM != lowerMagic)) {
                print("The magic value for this malloc node is incorrect!\n")
                print(this)
                return null
            }
            val size = sim.loadWordwCache(nodeAddr + lowBuffer + 4)
            val free = sim.loadWordwCache(nodeAddr + lowBuffer + 8)
            val nextNode = sim.loadWordwCache(nodeAddr + lowBuffer + 12)
            val prevNode = sim.loadWordwCache(nodeAddr + lowBuffer + 16)
            return MallocNode(
                    size = size,
                    free = free,
                    nextNode = nextNode,
                    prevNode = prevNode,
                    nodeAddr = nodeAddr
            )
        }
    }

    fun storeMagic(sim: Simulator) {
        sim.storeWordwCache(this.nodeAddr, lowerMagic)
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 20, upperMagic)
    }

    fun storeSize(sim: Simulator) {
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 4, this.size)
    }

    fun storeFree(sim: Simulator) {
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 8, this.free)
    }

    fun storeNextNode(sim: Simulator) {
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 12, this.nextNode)
    }

    fun storePrevNode(sim: Simulator) {
        sim.storeWordwCache(this.nodeAddr + lowBuffer + 16, this.prevNode)
    }

    fun storeNode(sim: Simulator) {
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

    fun canFit(size: Int): Boolean {
        return this.isFree() && (this.size >= size)
    }

    fun dataAddr(): Int {
        return this.nodeAddr + sizeof
    }

    fun getNextNode(sim: Simulator): MallocNode? {
        return loadBlock(sim, this.nextNode)
    }

    fun getPrevNode(sim: Simulator): MallocNode? {
        return loadBlock(sim, this.prevNode)
    }

    fun allocateNode(sim: Simulator, wantedSize: Int, calloc: Boolean=false): Boolean {
        if (this.size < wantedSize) {
            return false
        }
        this.free = 0
        if (calloc) {
            if (sim.memset(this.dataAddr(), 0, wantedSize) == 0) {
                return false
            }
        }
        if (this.size > wantedSize + MallocNode.sizeof + MallocNode.minSize) {
            val newNodeAddr = this.dataAddr() + wantedSize
            val newNodeSize = this.size - (wantedSize + MallocNode.sizeof)
            val newNode = MallocNode(newNodeSize, 1, this.nextNode, this.nodeAddr, newNodeAddr)
            val nextNode = getNextNode(sim)!!
            nextNode.prevNode = newNodeAddr
            this.nextNode = newNodeAddr
            this.size = wantedSize
            newNode.storeNode(sim)
            nextNode.storeNode(sim)
        }
        this.storeNode(sim)
        return true
    }

    fun freeNode(sim: Simulator) {
        this.free = 1
        this.storeFree(sim)
        var next: MallocNode = this
        var s = 0
        while (!next.isNextNull()) {
            next = next.getNextNode(sim)!!
            if (!next.isFree()) {
                break
            }
            s += MallocNode.sizeof + next.size
        }
        var prev = this
        while (!prev.isPrevNull()) {
            prev = prev.getPrevNode(sim)!!
            if (prev.isPrevNull()) { // Is this needed?
                break
            }
            if (!prev.isFree()) {
                break
            }
            s += MallocNode.sizeof + prev.size
        }
        if (this != next || this != prev) {
            if (this != prev) {
                s += MallocNode.sizeof + this.size
            }
            prev.size += s
            prev.nextNode = next.nodeAddr
            next.prevNode = prev.nodeAddr
            next.storeNode(sim)
            prev.storeNode(sim)
        }
    }
}

class Alloc(val sim: Simulator) {
    val sentinel = MallocNode(0, 0, 0, 0, 0)
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

    fun malloc(size: Int, calloc: Boolean = false): Int {
        if (size <= 0) {
            return 0
        }
        var m = this.sentinel
        while (!m.isNextNull()) {
            m = m.getNextNode(sim)!!
            if (m.canFit(size)) {
                if (m.allocateNode(sim, size, calloc)) {
                    return m.dataAddr()
                }
            }
        }
        // TODO DO rlim check

        val newMallocNode = MallocNode(size, 0, 0, m.nodeAddr, sim.getHeapEnd().toInt())
        sim.addHeapSpace(size)
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
        sim.memcpy(newBlock, m.dataAddr(), m.size)
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
        var counter = 0
        var m = sentinel
        while (!m.isNextNull()) {
            counter++
            // If we fail to get the next node when it should exist, we must return -1 to indicate an error.
            m = m.getNextNode(this.sim)?: return -1
        }
        return counter
    }
}