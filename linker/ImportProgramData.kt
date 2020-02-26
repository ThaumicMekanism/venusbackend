package venusbackend.linker

import venusbackend.riscv.Import

data class ImportProgramData(
    val progImport: Import,
    val importingProgram: Import,
    val importingProgramAbsPath: String
)