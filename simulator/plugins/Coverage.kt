package venusbackend.simulator.plugins

import kotlinx.serialization.Serializable
import venusbackend.linker.ProgramDebugInfo
import venusbackend.riscv.MachineCode
import venusbackend.simulator.Simulator

@Serializable
data class CoverageLine(val pc: Long, val loc: String, val count: Int)

class Coverage() : SimulatorPlugin {

    private val pcCount = mutableMapOf<Number, Int>()

    override fun reset(sim: Simulator) {
        pcCount.clear()
    }

    override fun init(sim: Simulator) {
        reset(sim)
    }

    override fun onStep(sim: Simulator, inst: MachineCode, prevPC: Number) {
        val newCount = (pcCount[prevPC] ?: 0) + 1
        pcCount[prevPC] = newCount
    }

    override fun finish(sim: Simulator, any: Any?): Any? {
        return finish(sim)
    }

    /** returns coverage lines */
    fun finish(sim: Simulator): List<CoverageLine> {
        // first we find all locations
        val allLocations = sim.linkedProgram.dbg.map { d -> getLocation(d) }

        // map location to count + pc
        val locationToCount = pcCount.map { (pc, count) -> getLocation(sim, pc) to count }.toMap()

        val lines = allLocations.withIndex().map { (idx, loc) ->
            val pc = instructionIndexToPc(sim, idx)
            val count = (locationToCount[loc] ?: 0)
//            "${toHex(pc)} $loc $count"
            CoverageLine(pc.toLong(), loc, count)
        }

        return lines
    }

    private fun getLocation(dbg: ProgramDebugInfo): String {
        return "${dbg.programName}:${dbg.dbg.lineNo}"
    }

    private fun getLocation(sim: Simulator, pc: Number): String {
        val idx = pcToInstructionIndex(sim, pc)
        val dbg = sim.linkedProgram.dbg[idx]
        return getLocation(dbg)
    }

    private fun pcToInstructionIndex(sim: Simulator, pc: Number): Int {
        return sim.invInstOrderMapping[pc]!!
    }
    private fun instructionIndexToPc(sim: Simulator, idx: Int): Number {
        return sim.instOrderMapping[idx]!!
    }
}