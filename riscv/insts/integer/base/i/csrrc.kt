package venusbackend.riscv.insts.integer.base.i

import venusbackend.numbers.toQuadWord
import venusbackend.riscv.InstructionField
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.base.CSRTypeParser
import venusbackend.simulator.SpecialRegisters

/*
    TODO: 
    -> avoid side effects in case rs1 == x0 [Table 9.1 in "Volume I: RISC-V Unprivileged ISA V20191213"]
 */
val csrrc = Instruction(
        name = "csrrc",
        format = InstructionFormat(4,
                listOf(
                        FieldEqual(InstructionField.OPCODE, 0b1110011),
                        FieldEqual(InstructionField.FUNCT3, 0b011)
                )
        ),
        parser = CSRTypeParser,
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val vrs1 = sim.getReg(mcode[InstructionField.RS1].toInt()).toInt()
            val vcsr = sim.getSReg(mcode[InstructionField.CSR]).toInt()

            // get csr[11:10] => read/write or read-only
            val rw_ro = (mcode[InstructionField.CSR].toInt() ushr 10) and 0x3
            // get csr[9:8] => lowest privilege level that can access the CSR
            val accessPrivLevel = (mcode[InstructionField.CSR].toInt() ushr 8) and 0x3

            // check if the CSR has read/write
            if( (rw_ro == 0x3) and (mcode[InstructionField.RS1].toInt() != 0) ) {
                // (CSR is read-only) AND (rs1 != x0) => exception!
                sim.setSReg(SpecialRegisters.MCAUSE.address, 1) // exception code: 'Instruction access fault'
                sim.handleMachineException()
            }
            // check if current PRIV level allows CSR access
            else if( sim.getPRIV().toInt() < accessPrivLevel ) {
                // CSR does not allow access in current priv level
                sim.setSReg(SpecialRegisters.MCAUSE.address, 2) // exception code: 'illegal instruction'
                sim.handleMachineException()
            }
            else {
                sim.setReg(mcode[InstructionField.RD].toInt(), vcsr)
                sim.setSReg(mcode[InstructionField.CSR], vrs1.inv() and vcsr)
                sim.incrementPC(mcode.length)
            }
        },
        impl64 = RawImplementation { mcode, sim ->
            val vrs1 = sim.getReg(mcode[InstructionField.RS1].toInt()).toLong()
            val vcsr = sim.getSReg(mcode[InstructionField.CSR]).toLong()

            // get csr[11:10] => read/write or read-only
            val rw_ro = (mcode[InstructionField.CSR].toInt() ushr 10) and 0x3
            // get csr[9:8] => lowest privilege level that can access the CSR
            val accessPrivLevel = (mcode[InstructionField.CSR].toInt() ushr 8) and 0x3

            // check if the CSR has read/write
            if( (rw_ro == 0x3) and (mcode[InstructionField.RS1].toInt() != 0) ) {
                // (CSR is read-only) AND (rs1 != x0) => exception!
                sim.setSReg(SpecialRegisters.MCAUSE.address, 1) // exception code: 'Instruction access fault'
                sim.handleMachineException()
            }
            // check if current PRIV level allows CSR access
            else if( sim.getPRIV().toInt() < accessPrivLevel ) {
                // CSR does not allow access in current priv level
                sim.setSReg(SpecialRegisters.MCAUSE.address, 2) // exception code: 'illegal instruction'
                sim.handleMachineException()
            }
            else {
                sim.setReg(mcode[InstructionField.RD].toInt(), vcsr)
                sim.setSReg(mcode[InstructionField.CSR], vrs1.inv() and vcsr)
                sim.incrementPC(mcode.length)
            }
        },
        impl128 = RawImplementation { mcode, sim ->
            val vrs1 = sim.getReg(mcode[InstructionField.RS1].toInt()).toQuadWord()
            val vcsr = sim.getSReg(mcode[InstructionField.CSR]).toQuadWord()

            // get csr[11:10] => read/write or read-only
            val rw_ro = (mcode[InstructionField.CSR].toInt() ushr 10) and 0x3
            // get csr[9:8] => lowest privilege level that can access the CSR
            val accessPrivLevel = (mcode[InstructionField.CSR].toInt() ushr 8) and 0x3

            // check if the CSR has read/write
            if( (rw_ro == 0x3) and (mcode[InstructionField.RS1].toInt() != 0) ) {
                // (CSR is read-only) AND (rs1 != x0) => exception!
                sim.setSReg(SpecialRegisters.MCAUSE.address, 1) // exception code: 'Instruction access fault'
                sim.handleMachineException()
            }
            // check if current PRIV level allows CSR access
            else if( sim.getPRIV().toInt() < accessPrivLevel ) {
                // CSR does not allow access in current priv level
                sim.setSReg(SpecialRegisters.MCAUSE.address, 2) // exception code: 'illegal instruction'
                sim.handleMachineException()
            }
            else {
                sim.setReg(mcode[InstructionField.RD].toInt(), vcsr)
                sim.setSReg(mcode[InstructionField.CSR], vrs1.inv() and vcsr)
                sim.incrementPC(mcode.length)
            }
        },
        disasm = RawDisassembler {
            val dest = it[InstructionField.RD]
            val source = it[InstructionField.RS1]
            //val csr = it[InstructionField.IMM_11_0]
            val csr = it[InstructionField.CSR]
            "csrrc x$dest $csr x$source"
        }
)