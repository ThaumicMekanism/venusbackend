package venusbackend.riscv

public enum class MemSize(val size: Int) {
    LONG(8),
    WORD(4),
    HALF(2),
    BYTE(1)
}