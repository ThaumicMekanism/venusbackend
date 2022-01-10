package venusbackend.riscv.insts.dsl.parsers.base

import venusbackend.assembler.DebugInfo
import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.Program
import venusbackend.riscv.insts.dsl.getImmediate
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
import kotlin.math.pow

object CSRITypeParser : InstructionParser {
    override operator fun invoke(prog: Program, mcode: MachineCode, args: List<String>, dbg: DebugInfo) {
        checkArgsLength(args.size, 3, dbg)

        mcode[InstructionField.RD] = regNameToNumber(args[0], dbg = dbg)        
        mcode[InstructionField.CSR] = regNameToNumber(args[1], dbg = dbg)
        mcode[InstructionField.RS1] = getImmediate(args[2], 0, ((2.0).pow(6 - 1).toInt()-1), dbg)
    
    }
}