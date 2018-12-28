package venusbackend.riscv.insts.dsl.formats

import venusbackend.riscv.InstructionField
import venusbackend.riscv.MachineCode

data class FieldEqual(val ifield: InstructionField, val required: Int)

open class InstructionFormat(val length: Int, val ifields: List<FieldEqual>) {
    fun matches(mcode: MachineCode): Boolean = ifields.all {
        (ifield, required) -> mcode[ifield].toInt() == required
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
