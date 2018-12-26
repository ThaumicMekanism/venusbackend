package venusbackend.riscv.insts.dsl.parsers.extensions

import venusbackend.assembler.AssemblerError
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber

/**
 * Created by thaum on 8/6/2018.
 */
object CSTypeParser : InstructionParser {
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>) {
        checkArgsLength(args.size, 2)

        val rdp = regNameToNumber(args[0])
        val rs2p = regNameToNumber(args[1])

        if (rdp !in 8..15 || rs2p !in 8..15) {
            throw AssemblerError("CS instructions only takes registers x8 to x15!")
        }

        mcode[InstructionField.RDP] = rdp - 8
        mcode[InstructionField.RS2P] = rs2p - 8
    }
}