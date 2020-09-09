package venusbackend.simulator

import venusbackend.linker.ProgramDebugInfo
import venusbackend.riscv.MachineCode
import venusbackend.toHex

class Coverage(private val sim: Simulator) : SimulatorPlugin {

    private val pcCount = mutableMapOf<Number, Int>()

    override fun onStep(inst: MachineCode, prevPC: Number) {
        val newCount = (pcCount[prevPC] ?: 0) + 1
        pcCount[prevPC] = newCount
    }

    /** returns coverage lines */
    fun finish(): List<String> {
        // first we find all locations
        val allLocations = sim.linkedProgram.dbg.map { d -> getLocation(d) }

        // map location to count + pc
        val locationToCount = pcCount.map { (pc, count) -> getLocation(pc) to count }.toMap()

        val lines = allLocations.withIndex().map { (idx, loc) ->
            val pc = instructionIndexToPc(idx)
            val count = (locationToCount[loc] ?: 0)
            "${toHex(pc)} $loc $count"
        }

        return lines
    }

    private fun getLocation(dbg: ProgramDebugInfo): String {
        return "${dbg.programName}:${dbg.dbg.lineNo}"
    }

    private fun getLocation(pc: Number): String {
        val idx = pcToInstructionIndex(pc)
        val dbg = sim.linkedProgram.dbg[idx]
        return getLocation(dbg)
    }

    private fun pcToInstructionIndex(pc: Number): Int {
        return sim.invInstOrderMapping[pc]!!
    }
    private fun instructionIndexToPc(idx: Int): Number {
        return sim.instOrderMapping[idx]!!
    }
}