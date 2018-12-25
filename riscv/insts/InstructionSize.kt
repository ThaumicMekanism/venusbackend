package venusbackend.riscv.insts

import venusbackend.assembler.AssemblerError

enum class InstructionSize(width: Int) {
    B16(16),
    B32(32),
    B64(64),
    B128(128);

    companion object {
        fun getInstSize(width: Int): InstructionSize {
            return when (width) {
                16 -> {B16}
                32 -> {B32}
                64 -> {B64}
                128 -> {B128}
                else -> {throw AssemblerError("Invalid instruction size $width!")}
            }
        }
    }
}