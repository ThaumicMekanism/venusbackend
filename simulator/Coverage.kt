package venusbackend.simulator

import venusbackend.linker.ProgramDebugInfo
import venusbackend.riscv.MachineCode
import venusbackend.toHex
import java.io.File

class Coverage(private val sim: Simulator, private val outputFile: String) : IsSimulatorPlugin {

    private val pcCount = mutableMapOf<Number, Int>()

    init {
        assert(outputFile.isNotBlank()) {"Blank output file for coverage: $outputFile"}
    }

    override fun onStep(inst: MachineCode, prevPC: Number) {
        val newCount = pcCount.getOrDefault(prevPC, 0) + 1
        pcCount[prevPC] = newCount
    }

    /** saves the coverage to the coverage file */
    fun finish() {
        // first we find all locations
        val allLocations = sim.linkedProgram.dbg.map { d -> getLocation(d) }

        // map location to count + pc
        val locationToCount = pcCount.map { (pc, count) -> getLocation(pc) to count }.toMap()

        val lines = allLocations.withIndex().map { (idx, loc) ->
            val pc = instructionIndexToPc(idx)
            val count = locationToCount.getOrDefault(loc, 0)
            "${toHex(pc)} $loc $count"
        }

        File(outputFile).bufferedWriter().use { out ->
            lines.forEach { line -> out.appendln(line) }
        }
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