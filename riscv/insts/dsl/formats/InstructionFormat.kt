package venusbackend.riscv.insts.dsl.formats

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode

data class FieldEqual(val ifield: InstructionField, val required: Int, val not: Boolean = false, val oifields: List<InstructionField> = listOf())

open class InstructionFormat(val length: Int, val ifields: List<FieldEqual>) {
    fun matches(mcode: MachineCode): Boolean = ifields.all {
        (ifield, required, bool, oifields) -> if (bool) {
//            mcode[ifield].toInt() != required
            var res = mcode[ifield].toInt() != required
            for (ifild in oifields) {
                res = res and (mcode[ifield].toInt() != required)
            }
            res
        } else {
//            mcode[ifield].toInt() == required
            var res = mcode[ifield].toInt() == required
            for (ifild in oifields) {
                res = res and (mcode[ifield].toInt() == required)
            }
            res
        }
    }

    fun fill(): MachineCode {
        val mcode = MachineCode(0)
        mcode.length = length
        for ((ifield, required) in ifields) {
            mcode[ifield] = required
        }
        return mcode
    }
}
