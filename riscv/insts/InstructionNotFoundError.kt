package venusbackend.riscv.insts

class InstructionNotFoundError : Throwable {
    constructor(msg: String? = null) : super(msg)
}