package venusbackend.riscv.insts.dsl.parsers.base

/* ktlint-disable no-wildcard-imports */
import venusbackend.assembler.AssemblerError
import venusbackend.riscv.*
import venusbackend.riscv.insts.dsl.getImmWarning
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
/* ktlint-enable no-wildcard-imports */

object BTypeParser : InstructionParser {
    const val B_TYPE_MIN = -2048
    const val B_TYPE_MAX = 2047
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>) {
        checkArgsLength(args.size, 3)

        mcode[InstructionField.RS1] = regNameToNumber(args[0])
        mcode[InstructionField.RS2] = regNameToNumber(args[1])

        val label = args[2]
        var imm: Int? = null
        try {
            imm = prog.getLabelOffset(label)
                    ?: throw AssemblerError("could not find label $label")
        } catch (e: AssemblerError) {
            try {
                imm = prog.getLabelOffset(venusInternalLabels + (userStringToInt(label) + prog.textSize))
                getImmWarning += "Interpreting the label as an offset!; "
            } catch (e2: Throwable) {
                throw e
            }
        }

        if (imm !in B_TYPE_MIN..B_TYPE_MAX) {
            getImmWarning = getImmWarning.replace("Interpreting the label as an offset!; ", "")
            throw AssemblerError("branch to $label too far")
        }

        mcode[InstructionField.IMM_11_B] = imm !! shr 11
        mcode[InstructionField.IMM_4_1] = imm shr 1
        mcode[InstructionField.IMM_12] = imm shr 12
        mcode[InstructionField.IMM_10_5] = imm shr 5
    }
}
