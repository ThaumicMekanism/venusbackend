package venusbackend.riscv

public enum class MemSize(val size: Int) {
    QUAD(16),
    LONG(8),
    WORD(4),
    HALF(2),
    BYTE(1)
}