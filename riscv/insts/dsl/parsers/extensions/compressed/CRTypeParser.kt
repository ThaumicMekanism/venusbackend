package venusbackend.riscv.insts.dsl.parsers.extensions.compressed

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber

/**
 * Created by thaum on 8/6/2018.
 */
object CRTypeParser : InstructionParser {
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 2)

        val crd = regNameToNumber(args[0])
        val crs2 = regNameToNumber(args[1])

        mcode[InstructionField.RD] = crd
        mcode[InstructionField.CRS2] = crs2
    }
}