package venusbackend.riscv.insts.integer.base.uj

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.dsl.Instruction
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.OpcodeFormat
import venusbackend.riscv.insts.dsl.getImmWarning
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.impls.setBitslice
import venusbackend.riscv.insts.dsl.impls.signExtend
import venusbackend.riscv.insts.dsl.parsers.RawParser
import venusbackend.riscv.insts.dsl.parsers.checkArgsLength
import venusbackend.riscv.insts.dsl.parsers.regNameToNumber
import venusbackend.riscv.insts.dsl.relocators.JALRelocator
import venusbackend.riscv.userStringToInt
import venusbackend.riscv.venusInternalLabels

val jal = Instruction(
        name = "jal",
        format = OpcodeFormat(0b1101111),
        parser = RawParser { prog, mcode, args ->
            checkArgsLength(args.size, 2)

            mcode[InstructionField.RD] = regNameToNumber(args[0])
            val label = if (prog.labels.containsKey(args[1])) {
                args[1]
            } else {
                try {
                    getImmWarning += "Interpreting label as offset!; "
                    venusInternalLabels + (prog.textSize + userStringToInt(args[1]))
                } catch (e: Throwable) {
                    getImmWarning = getImmWarning.replace("Interpreting label as offset!; ", "")
                    args[1]
                }
            }
            prog.addRelocation(JALRelocator, label)
        },
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val imm = constructJALImmediate(mcode)
            sim.setReg(rd, sim.getPC().toInt() + mcode.length)
            sim.incrementPC(imm)
            sim.jumped = true
        },
        impl64 = RawImplementation { mcode, sim ->
            val rd = mcode[InstructionField.RD].toInt()
            val imm = constructJALImmediate(mcode).toLong()
            sim.setReg(rd, sim.getPC().toLong() + mcode.length)
            sim.incrementPC(imm)
            sim.jumped = true
        },
        impl128 = NoImplementation,
        disasm = RawDisassembler { mcode ->
            val rd = mcode[InstructionField.RD]
            val imm = constructJALImmediate(mcode)
            "jal x$rd $imm"
        }
)

private fun constructJALImmediate(mcode: MachineCode): Int {
    val imm_20 = mcode[InstructionField.IMM_20].toInt()
    val imm_10_1 = mcode[InstructionField.IMM_10_1].toInt()
    val imm_11 = mcode[InstructionField.IMM_11_J].toInt()
    val imm_19_12 = mcode[InstructionField.IMM_19_12].toInt()
    var imm = 0
    imm = setBitslice(imm, imm_20, 20, 21)
    imm = setBitslice(imm, imm_10_1, 1, 11)
    imm = setBitslice(imm, imm_11, 11, 12)
    imm = setBitslice(imm, imm_19_12, 12, 20)
    return signExtend(imm, 21)
}
