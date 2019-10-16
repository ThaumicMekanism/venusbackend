package venusbackend.riscv

/* ktlint-disable no-wildcard-imports */
import venusbackend.riscv.insts.floating.double.i.fld
import venusbackend.riscv.insts.floating.double.r.*
import venusbackend.riscv.insts.floating.double.s.fsd
import venusbackend.riscv.insts.floating.fcvtds
import venusbackend.riscv.insts.floating.fcvtsd
import venusbackend.riscv.insts.floating.single.i.flw
import venusbackend.riscv.insts.floating.single.r.*
import venusbackend.riscv.insts.floating.single.s.fsw
import venusbackend.riscv.insts.integer.base.i.*
import venusbackend.riscv.insts.integer.base.i.ecall.ecall
import venusbackend.riscv.insts.integer.base.r.*
import venusbackend.riscv.insts.integer.base.sb.*
import venusbackend.riscv.insts.integer.base.s.*
import venusbackend.riscv.insts.integer.base.u.*
import venusbackend.riscv.insts.integer.base.uj.*
import venusbackend.riscv.insts.integer.extensions.atomic.r.*
import venusbackend.riscv.insts.integer.extensions.compressed.ca.*
import venusbackend.riscv.insts.integer.extensions.compressed.cr.*
import venusbackend.riscv.insts.integer.extensions.multiply.r.*
/* ktlint-enable no-wildcard-imports */

class InitInstructions {
    companion object {
        var inited = false
    }
    init {
        if (!inited) {
            addi
            addiw
            andi
            csrrc
            csrrci
            csrrw
            csrrwi
            ebreak
            ecall
            fence
            fencei
            jalr
            lb
            lbu
            ld
            lh
            lhu
            lw
//            lwu
            ori
            slli
            slliw
            slti
            sltiu
            srai
            sraiw
            srli
            srliw
            xori
            add
            addw
            and
            or
            sll
            sllw
            slt
            sltu
            sra
            sraw
            srl
            srlw
            sub
            subw
            xor
            sb
            sd
            sh
            sw
            beq
            bge
            bgeu
            blt
            bltu
            bne
            auipc
            lui
            jal
            amoaddw
            amoandw
            amomaxw
            amomaxuw
            amominw
            amominuw
            amoorw
            amoswapw
            amoxorw
            lrw
            scw
            div
            divu
//            divw
            mul
            mulh
            mulhsu
            mulhu
//            mulw
            rem
            remu
//            remuw
//            remw
            fld
            faddd
            fclassd
            fcvtdw
            fcvtdwu
            fcvtwd
            fcvtwud
            feqd
            fled
            fltd
            fmaddd
            fmaxd
            fmind
            fmsubd
            fmuld
            fmvdx
            fmvxd
            fnmaddd
            fnmsubd
            fsgnjd
            fsgnjnd
            fsgnjxd
            fsqrtd
            fsubd
            fsd
            flw
            fadds
            fclasss
            fcvtsw
            fcvtswu
            fcvtws
            fcvtwus
            fdivs
            feqs
            fles
            flts
            fmadds
            fmaxs
            fmins
            fmsubs
            fmuls
            fmvsx
            fmvxs
            fnmadds
            fnmsubs
            fsgnjs
            fsgnjns
            fsgnjxs
            fsqrts
            fsubs
            fsw
            fcvtds
            fcvtsd
            caddw
            cand
            cor
            csub
            csubw
            cxor
            cadd
            cebreak
            cmv
            inited = true
        }
    }
}