package venusbackend.riscv.insts.dsl

import venusbackend.riscv.insts.dsl.disasms.base.STypeDisassembler
import venusbackend.riscv.insts.dsl.formats.base.STypeFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.base.b32.STypeImplementation32
import venusbackend.riscv.insts.dsl.impls.base.b64.STypeImplementation64
import venusbackend.riscv.insts.dsl.parsers.base.STypeParser
import venusbackend.simulator.Simulator

class STypeInstruction(
    name: String,
    opcode: Int,
    funct3: Int,
    store16: (Simulator, Short, Short) -> Unit = { _, _, _ -> throw NotImplementedError("no rv16") },
    store32: (Simulator, Int, Int) -> Unit,
    store64: (Simulator, Long, Long) -> Unit = { _, _, _ -> throw NotImplementedError("no rv64") },
    store128: (Simulator, Long, Long) -> Unit = { _, _, _ -> throw NotImplementedError("no rv128") }
) : Instruction(
        name = name,
        format = STypeFormat(opcode, funct3),
        parser = STypeParser,
        impl16 = NoImplementation,
        impl32 = STypeImplementation32(store32),
        impl64 = STypeImplementation64(store64),
        impl128 = NoImplementation,
        disasm = STypeDisassembler
)
