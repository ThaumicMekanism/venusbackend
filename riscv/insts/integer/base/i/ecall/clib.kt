package venusbackend.riscv.insts.integer.base.i.ecall

import venus.Renderer
import venusbackend.riscv.Registers
import venusbackend.simulator.Simulator

fun clib(sim: Simulator) {
    val whichCall = sim.getReg(Registers.a6)
    when (whichCall) {
        1 -> malloc(sim)
        2 -> calloc(sim)
        3 -> realloc(sim)
        4 -> free(sim)
        5 -> mallocActiveNumBlocks(sim)
        else -> Renderer.printConsole("Invalid clib ecall $whichCall")
    }
}

/**
 * Takes in
 * a1 size to malloc.
 *
 * Returns to a0 a pointer to the new malloced data or NULL if the malloc has failed.
 */
fun malloc(sim: Simulator) {
    val size = sim.getReg(Registers.a1).toInt()
    val dest = sim.alloc.malloc(size)
    sim.setReg(Registers.a0, dest)
}

/**
 * Takes in:
 * a1 nitems
 * a2 size
 *
 * Returns to a0 a pointer to the new malloced data or NULL if the calloc has failed.
 */
fun calloc(sim: Simulator) {
    val nitems = sim.getReg(Registers.a1).toInt()
    val size = sim.getReg(Registers.a2).toInt()
    val dest = sim.alloc.calloc(nitems, size)
    sim.setReg(Registers.a0, dest)
}

/**
 * Takes in:
 * a1 ptr
 * a2 size
 *
 * Returns to a0 a pointer to the new malloced data or NULL if the realloc has failed.
 */
fun realloc(sim: Simulator) {
    val ptr = sim.getReg(Registers.a1).toInt()
    val size = sim.getReg(Registers.a2).toInt()
    val dest = sim.alloc.realloc(ptr, size)
    sim.setReg(Registers.a0, dest)
}

/**
 * Takes in:
 * a1 ptr
 *
 * Free does not return anything
 */
fun free(sim: Simulator) {
    val ptr = sim.getReg(Registers.a1).toInt()
    sim.alloc.free(ptr)
}

/**
 * Takes in nothing
 *
 * Returns the number of blocks which are not free or -1 if there was an error
 * Errors are currently caused by the list to be incorrectly modified.
 */
fun mallocActiveNumBlocks(sim: Simulator) {
    val amt = sim.alloc.numActiveBlocks()
    sim.setReg(Registers.a0, amt)
}