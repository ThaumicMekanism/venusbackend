package venusbackend.riscv

import venusbackend.assembler.AssemblerError
import venusbackend.assembler.DebugInfo
import venusbackend.linker.DataRelocationInfo
import venusbackend.linker.RelocationInfo
import venusbackend.riscv.insts.dsl.relocators.Relocator

data class Import(val path: String, val relative: Boolean)

/**
 * An (unlinked) program.
 *
 * @param name the name of the program, used for debug info
 * @see venus.assembler.Assembler
 * @see venus.linker.Linker
 */
public val venusInternalLabels: String = "Venus_Internal_Label-"
class Program(var name: String = "anonymous", val absPath: String) {
    /* TODO: abstract away these variables */
    val insts = ArrayList<MachineCode>()
    val debugInfo = ArrayList<DebugInfo>()
    val labels = HashMap<String, Int>()
    val equivs = HashMap<String, String>()
    val relocationTable = ArrayList<RelocationInfo>()
    val dataRelocationTable = ArrayList<DataRelocationInfo>()
    val dataSegment = ArrayList<Byte>()
    val localReferences = HashMap<Int, MutableSet<Int>>() // <Numeric Label, Addresses of that label>
    var textSize = 0
    var dataSize = 0
    private val globalLabels = HashSet<String>()
    val imports = ArrayList<Import>()

    /**
     * Adds an instruction to the program, and increments the text size.
     *
     * @param mcode the instruction to add
     */
    fun add(mcode: MachineCode) {
        insts.add(mcode)
        textSize += mcode.length
    }

    /**
     * Adds a byte of data to the program, and increments the data size.
     *
     * @param byte the byte to add
     */
    fun addToData(byte: Byte) {
        dataSegment.add(byte)
        dataSize++
    }

    /**
     * Overwrites a byte of data in the program's data segment
     *
     * @param offset the offset at which to overwrite
     * @param byte the value to overwrite with
     */
    fun overwriteData(offset: Int, byte: Byte) {
        dataSegment[offset] = byte
    }

    fun addImport(filepath: String, relative: Boolean) {
        this.imports.add(Import(filepath, relative))
    }

    /**
     * Adds debug info to the instruction currently being assembled.
     *
     * In the case of pseudo-instructions, the original instruction will be added multiple times.
     * @todo Find a better way to deal with pseudoinstructions
     *
     * @param dbg the debug info to add
     */
    fun addDebugInfo(dbg: DebugInfo) {
        while (debugInfo.size < insts.size) {
            debugInfo.add(dbg)
        }
    }

    /**
     * Adds a label with a given offset to the program.
     *
     * @param label the label to add
     * @param offset the byte offset to add it at (from the start of the program)
     */
    fun addLabel(label: String, offset: Int): Int? {
        if (label.matches(Regex("\\d+"))) {
            val intlabel = label.toInt()
            if (localReferences.containsKey(intlabel)) {
                val set = localReferences[intlabel]!!
                set.add(offset)
            } else {
                val new_set = HashSet<Int>()
                new_set.add(offset)
                localReferences[intlabel] = new_set
            }
            return null
        } else {
            return labels.put(label, offset)
        }
    }

    /**
     * Adds a symbol defined by .equiv, .equ, or .set.
     */
    fun addEqu(label: String, defn: String) = equivs.put(label, defn)
    private val SYM_PATN = Regex("""(.*?)(?:([-+])(?:(\d+)|(.*)))?$""")
    /** Return the symbolic part of LABELARG, where LABELARG may be either
     *  <symbol>, <symbol>+<decimal numeral>, or <symbol>-<decimal numeral>.
     */
    fun symbolPart(labelArg: String, dbg: DebugInfo): String {
        val match = SYM_PATN.find(labelArg) ?: throw AssemblerError("bad symbol reference: $labelArg", dbg)
        return match.groupValues[1]
    }
    /** Return the numeric offset part of LABELARG, where LABELARG may be either
     *  <symbol> (result 0), <symbol>+<decimal numeral> (result
     *  <decimal numeral> as an Int), <symbol>-<decimal numeral>,
     *   <symbol>+<absolute symbol>, or <symbol>-<abssolute symbol>.  Here,
     *   <absolute symbol> refers to a .equiv'ed symbol that resolves to
     *   an immediate constant (as in .equiv value, 13).
     */
    fun labelOffsetPart(labelArg: String, dbg: DebugInfo): Int {
        val match = SYM_PATN.find(labelArg) ?: throw AssemblerError("ill-formed symbol reference: $labelArg", dbg)
        val (_, sign, num, offsetSym) = match.destructured
        if (sign == "") {
            return 0
        }
        if (num != "") {
            return (sign + num).toInt()
        }
        if (offsetSym !in labels) {
            throw AssemblerError("undefined symbol: $offsetSym", dbg)
        }
        return if (sign == "-") -labels[offsetSym]!! else labels[offsetSym]!!
    }

    /**
     * Gets the _relative_ label offset, or null if it does not exist.
     *
     * The _relative_ offset is relative to the instruction currently being assembled.
     *
     * @param label the label to find
     * @returns the relative offset, or null if it does not exist.
     */
    fun getLabelOffset(label: String, address: Int, dbg: DebugInfo): Int? {
        // TODO FIX ME TO WORK WITH FORWARD AND BACKWARD LOCAL REFERENCE
        val loc = if (label.matches(Regex("\\d+[fb]"))) {
            val intlabel = label.substring(0, label.length - 1).toInt()
            val number_set = localReferences[intlabel] ?: throw AssemblerError("The number label '$intlabel' has not been defined!", dbg)
            if (label.matches(Regex("\\d+f"))) {
                number_set.filter { it >= address }.min()
            } else {
                number_set.filter { it <= address }.max()
            }
        } else {
            labels.get(label)
        }
        return loc?.minus(textSize)
    }

    /**
     * Gets the immediate from a string, checking if it is in range.
     *
     * @param str the immediate as a string
     * @param min the minimum allowable value of the immediate
     * @param max the maximum allowable value of the immediate
     * @return the immediate, as an integer
     *
     * @throws IllegalArgumentException if the wrong number of arguments is given
     */
    internal fun getImmediate(str: String, min: Int, max: Int, dbg: DebugInfo): Int {
        val imm = try {
            userStringToInt(str)
        } catch (e: NumberFormatException) {
            val sym = symbolPart(str, dbg)
            val offsetVal = labelOffsetPart(str, dbg)
            if (sym != "" && sym !in labels) {
                throw AssemblerError("undefined symbol: $sym", dbg)
            }
            val symVal = if (sym == "") 0 else labels[sym]!!
            symVal + offsetVal
        }
        if (imm !in min..max)
            throw AssemblerError("immediate $str (= $imm) out of range (should be between $min and $max)", dbg)
        return imm
    }

    /**
     * Adds a line to the relocation table.
     *
     * @param label the label to relocate
     * @param labelOffset amount to add to the label value before applying
     *                    relocation
     * @param offset the byte offset at which to apply the relocation
     *               (from the start of the program)
     */
    fun addRelocation(
        relocator: Relocator,
        label: String,
        labelOffset: Int,
        offset: Int = textSize,
        dbg: DebugInfo
    ) = relocationTable.add(RelocationInfo(relocator, offset, label, labelOffset, dbg))

    /**
     * Adds a line to the data relocation table.
     *
     * @param label the label to relocate
     * @param labelOffset amount to add to the label value before applying
     *                    relocation
     * @param offset the byte offset at which to apply the relocation
     *               (from the start of the program)
     */
    fun addDataRelocation(
        label: String,
        labelOffset: Int,
        offset: Int = textSize,
        dbg: DebugInfo
    ) = dataRelocationTable.add(DataRelocationInfo(offset, label, labelOffset, dbg))

    /**
     * Makes a label global.
     *
     * @param label the label to make global
     */
    fun makeLabelGlobal(label: String) {
        globalLabels.add(label)
    }

    /**
     * Checks if a label is global.
     *
     * @param label the label to check
     * @return true if the label is global
     */
    fun isGlobalLabel(label: String) = globalLabels.contains(label)

    /**
     * checks if address is of a global label
     */
    fun isAddrGlobalLabel(addr: Number): Boolean {
        for (label in this.globalLabels) {
            if (addr == this.labels[label]) {
                return true
            }
        }
        return false
    }

    /* TODO: add dump formats */
    /**
     * Dumps the instructions.
     *
     * @return a list of instructions in this program
     */
    fun dump(): List<MachineCode> = insts

    fun assembleDependencies(): ArrayList<Program> {
        return ArrayList()
    }
}
