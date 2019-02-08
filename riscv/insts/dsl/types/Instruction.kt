package venusbackend.riscv.insts.dsl.types

import venusbackend.assembler.AssemblerError
import venusbackend.riscv.MachineCode
import venusbackend.riscv.insts.InstructionNotFoundError
import venusbackend.riscv.insts.dsl.disasms.InstructionDisassembler
import venusbackend.riscv.insts.dsl.formats.InstructionFormat
import venusbackend.riscv.insts.dsl.impls.InstructionImplementation
import venusbackend.riscv.insts.dsl.parsers.InstructionParser
import venusbackend.simulator.SimulatorError

open class Instruction(
    val name: String,
    val format: InstructionFormat,
    val parser: InstructionParser,
    val impl16: InstructionImplementation,
    val impl32: InstructionImplementation,
    val impl64: InstructionImplementation,
    val impl128: InstructionImplementation,
    val disasm: InstructionDisassembler
) {
    companion object {
        private val allInstructions = arrayListOf<Instruction>()

        operator fun get(mcode: MachineCode): Instruction =
                allInstructions.filter { it.format.length == mcode.length }
                        .firstOrNull { it.format.matches(mcode) }
                        ?: throw SimulatorError("instruction not found for 0x" + mcode.toString(16), InstructionNotFoundError())

        operator fun get(name: String) =
                allInstructions.firstOrNull { it.name == name }
                        ?: throw AssemblerError("instruction with name $name not found", InstructionNotFoundError())
    }

    init {
        allInstructions.add(this)
    }

    override fun toString() = name
}
