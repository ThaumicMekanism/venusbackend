package venusbackend.linker

import venusbackend.assembler.AssemblerError
import venusbackend.assembler.DebugInfo
import venusbackend.riscv.MemorySegments
import venusbackend.riscv.insts.dsl.relocators.Relocator

/**
 * Describes how to relocate a given instructions
 *
 * @param relocator the relocator to use
 * @param label the target label
 * @param offset the byte offset the instruction is at
 */
data class RelocationInfo(val relocator: Relocator, val offset: Int, val label: String, val labelOffset: Int, val dbg: DebugInfo)

/**
 * Describes how to relocate data bytes
 *
 * @param offset the byte offset in the data segment
 * @param label the target label
 */
data class DataRelocationInfo(val offset: Int, val label: String, val labelOffset: Int, val dbg: DebugInfo)

/**
 * A singleton which links a list of programs into one program.
 *
 * @see LinkedProgram
 */
object Linker {
    /**
     * Links together the given list of programs.
     *
     * This consists of several steps:
     *      - Collect all the labels in each program.
     *      - Keep track of all the instructions which need to be relocated.
     *      - Append all the instructions together.
     *      - Append all of the debugging info together.
     *      - Append all of the data segments together
     * Once the above is done, all the appropriate relocation is performed.
     *
     * @param progs a list of programs which will be linked
     * @return a [LinkedProgram] which can be used in the [venus.simulator.Simulator]
     * @todo refactor this into multiple functions
     */
    fun link(progsAndLibs: ProgramAndLibraries): LinkedProgram {
        val linkedProgram = LinkedProgram()
        val globalTable = HashMap<String, Int>()
        val toRelocate = ArrayList<RelocationInfo>()
        val toRelocateData = ArrayList<DataRelocationInfo>()
        var textTotalOffset = 0
        var dataTotalOffset = 0

        val progs = progsAndLibs.AllProgs

        if (progs.size > 0) {
            linkedProgram.prog.name = progs[0].name
        }
        for (prog in progs) {
            for ((label, offset) in prog.labels) {
                if (prog.isGlobalLabel(label)) {
                    val start = if (offset >= MemorySegments.STATIC_BEGIN) {
                        dataTotalOffset
                    } else {
                        textTotalOffset
                    }
                    val location = start + offset

                    linkedProgram.prog.addLabel(label, location)
                    linkedProgram.prog.makeLabelGlobal(label)
                    val previousValue = globalTable.put(label, location)
                    if (previousValue != null) {
                        throw AssemblerError("label $label defined global in two different files")
                    }
                    if (label == "main") {
                        linkedProgram.prog.makeLabelGlobal("main")
                        linkedProgram.startPC = location + MemorySegments.TEXT_BEGIN
                    }
                }
            }

            prog.insts.forEach(linkedProgram.prog::add)
            prog.debugInfo.forEach {
                linkedProgram.dbg.add(ProgramDebugInfo(prog.name, it))
            }
            prog.dataSegment.forEach(linkedProgram.prog::addToData)

            for ((relocator, offset, label, labelOffset, dbg) in prog.relocationTable) {
                val location = textTotalOffset + offset
                val mcode = linkedProgram.prog.insts[location / 4]
                if (label == "") {
                    relocator(mcode, location, labelOffset, dbg = dbg)
                } else {
                    val toAddress = prog.labels.get(label)
                    if (toAddress != null) {
                        /* TODO: fix this for variable length instructions */
                        val textOrDataOffset = if (toAddress >= MemorySegments.STATIC_BEGIN) {
                            dataTotalOffset
                        } else {
                            textTotalOffset
                        }
                        val target = toAddress + labelOffset + textOrDataOffset
                        relocator(mcode, location, target, dbg = dbg)
                    } else {
                        /* need to relocate globally */
                        toRelocate.add(RelocationInfo(
                                relocator, location,
                                label, labelOffset, dbg))
                    }
                }

//                val toAddress = prog.labels.get(label)
//                val location = textTotalOffset + offset
//                if (toAddress != null) {
//                    /* TODO: fix this for variable length instructions */
//                    val mcode = linkedProgram.prog.insts[location / 4]
//                    relocator(mcode, location, toAddress + textTotalOffset)
//                } else {
//                    /* need to relocate globally */
//                    toRelocate.add(RelocationInfo(relocator, location, label))
//                }
            }

            for ((offset, label, labelOffset, dbg) in prog.dataRelocationTable) {
                val toAddress0 = prog.labels.get(label)
                val location = dataTotalOffset + offset
                if (toAddress0 != null) {
                    val toAddress = toAddress0 + labelOffset
                    linkedProgram.prog.overwriteData(location, toAddress.toByte())
                    linkedProgram.prog.overwriteData(
                            location + 1,
                            (toAddress shr 8).toByte())
                    linkedProgram.prog.overwriteData(
                            location + 2,
                            (toAddress shr 16).toByte())
                    linkedProgram.prog.overwriteData(
                            location + 3,
                            (toAddress shr 24).toByte())
                } else {
                    /* need to relocate globally */
                    toRelocateData.add(DataRelocationInfo(
                            location, label, labelOffset, dbg))
                }
            }
            textTotalOffset += prog.textSize
            dataTotalOffset += prog.dataSize
            /* These two last lines are an ugly hack to fix the la of an import file generating a fixed offset already. */
//            dataTotalOffset += if (prog != progs.last()) {
//                for (i in 0 until prog.textSize) {
//                    linkedProgram.prog.addToData(0)
//                }
//                prog.dataSize + prog.textSize
//            } else {
//                prog.dataSize
//            }
        }

        for ((relocator, offset, label, labelOffset, dbg) in toRelocate) {
            val toAddress = globalTable.get(label)
                    ?: throw AssemblerError("label $label used but not defined", dbg)

            val mcode = linkedProgram.prog.insts[offset / 4]
            relocator(mcode, offset, toAddress, dbg = dbg)
        }

        for ((location, label, labelOffset, dbg) in toRelocateData) {
            val toAddress = globalTable.get(label) ?: throw AssemblerError("label $label used but not defined", dbg)
            linkedProgram.prog.overwriteData(location, toAddress.toByte())
            linkedProgram.prog.overwriteData(location + 1, (toAddress shr 8).toByte())
            linkedProgram.prog.overwriteData(location + 2, (toAddress shr 16).toByte())
            linkedProgram.prog.overwriteData(location + 3, (toAddress shr 24).toByte())
        }

        return linkedProgram
    }
}
