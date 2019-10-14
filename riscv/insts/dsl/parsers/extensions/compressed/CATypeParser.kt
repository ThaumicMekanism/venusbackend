package venusbackend.riscv.insts.dsl.parsers.extensions.compressed

import venusbackend.assembler.AssemblerError
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
object CATypeParser : InstructionParser {
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 2)

        val rdp = regNameToNumber(args[0])
        val rs2p = regNameToNumber(args[1])

        if (rdp !in 8..15 || rs2p !in 8..15) {
            throw AssemblerError("CA instructions only takes registers x8 to x15!")
        }

        mcode[InstructionField.RDP] = rdp
        mcode[InstructionField.RS2P] = rs2p
    }
}