package venusbackend.riscv.insts.dsl

import venusbackend.riscv.insts.dsl.disasms.base.LoadDisassembler
import venusbackend.riscv.insts.dsl.formats.base.ITypeFormat
import venusbackend.riscv.insts.dsl.impls.base.b32.LoadImplementation32
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.base.b64.LoadImplementation64
import venusbackend.riscv.insts.dsl.parsers.base.LoadParser
import venusbackend.simulator.Simulator

class LoadTypeInstruction(
    name: String,
    opcode: Int,
    funct3: Int,
    load16: (Simulator, Short) -> Short = { _, _ -> throw NotImplementedError("no rv16") },
    postLoad16: (Short) -> Short = { it },
    load32: (Simulator, Int) -> Int,
    postLoad32: (Int) -> Int = { it },
    load64: (Simulator, Long) -> Long = { _, _ -> throw NotImplementedError("no rv64") },
    postLoad64: (Long) -> Long = { it },
    load128: (Simulator, Long) -> Long = { _, _ -> throw NotImplementedError("no rv128") },
    postLoad128: (Long) -> Long = { it }
) : Instruction(
        name = name,
        format = ITypeFormat(opcode, funct3),
        parser = LoadParser,
        impl16 = NoImplementation,
        impl32 = LoadImplementation32(load32, postLoad32),
        impl64 = LoadImplementation64(load64, postLoad64),
        impl128 = NoImplementation,
        disasm = LoadDisassembler
)
