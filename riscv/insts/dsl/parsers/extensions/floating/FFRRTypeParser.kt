package venusbackend.riscv.insts.dsl.parsers.extensions.floating

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
object FFRRTypeParser : InstructionParser {
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 3, dbg)

        mcode[InstructionField.RD] = regNameToNumber(args[0], dbg = dbg)
        mcode[InstructionField.RS1] = regNameToNumber(args[1], false, dbg)
        mcode[InstructionField.RS2] = regNameToNumber(args[2], false, dbg)
    }
}