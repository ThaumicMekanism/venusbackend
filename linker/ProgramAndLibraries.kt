package venusbackend.linker

import venusbackend.assembler.AssemblerError
import venus.Driver.assemble
import venus.vfs.VFSFile
import venus.vfs.VFSProgram
import venus.vfs.VFSType
import venus.vfs.VirtualFileSystem
import venusbackend.assembler.Assembler
import venusbackend.riscv.Import
import venusbackend.riscv.Program

class ProgramAndLibraries(val progs: List<Program>, vfs: VirtualFileSystem) {
    val AllProgs = ArrayList<Program>()
    init {
        val seenPrograms = HashMap<Import, ImportProgramData>()
        val needToImportPrograms = HashMap<Import, ImportProgramData>()
//        val mainProg = "the main program"
        for (prog in progs) {
            val imp = Import(prog.name, false)
            seenPrograms[imp] = ImportProgramData(imp, Import(prog.absPath, false), prog.absPath)
            addProgramImports(prog, Import(prog.absPath, false), prog.absPath, seenPrograms, needToImportPrograms)
            AllProgs.add(prog)
        }
        val keysToRemove = HashSet<Import>()
        for (sprog in seenPrograms.values) {
            if (needToImportPrograms.containsKey(sprog.progImport)) {
                keysToRemove.add(sprog.progImport)
            }
        }
        for (ktr in keysToRemove) {
            needToImportPrograms.remove(ktr)
        }
        while (needToImportPrograms.isNotEmpty()) {
            val progImport = needToImportPrograms.keys.elementAt(0)
            val progname = progImport.path
            val progData = needToImportPrograms[progImport]!!
            needToImportPrograms.remove(progImport)
            // Get the file
            val obj = if (progImport.relative) {
                val parentNode = vfs.getObjectFromPath(progData.importingProgramAbsPath) ?: throw AssemblerError("Could not find the library: $progname from the relative path from the file. Parent file not found! This library was imported by ${progData.importingProgram}.")
                vfs.getObjectFromPath(progname, location = parentNode.parent) ?: throw AssemblerError("Could not find the library: $progname from the relative path from the file. This library was imported by ${progData.importingProgram}.")
            } else {
                vfs.getObjectFromPath(progname) ?: throw AssemblerError("Could not find the library: $progname from the absolute path (CWD). This library was imported by ${progData.importingProgram}.")
            }

            val prog = when (obj.type) {
                VFSType.File -> {
                    // Get the text
                    val progText = (obj as VFSFile).readText()
                    val (prog, errors, warnings) = Assembler.assemble(progText, progname, obj.getPath())
                    if (errors.isNotEmpty()) {
                        var msgs = "Could not load the library: $progname (Note: The library file was found but it could not be assembled) This library was imported by ${progData.importingProgram}. Here are a list of errors which may help:\n\n"
                        for (error in errors) {
                            msgs += "${error.message}\n\n"
                        }
                        throw AssemblerError(msgs)
                    }
                    prog
                }
                VFSType.Program -> {
                    (obj as VFSProgram).getProgram()
                }
                else -> {
                    throw AssemblerError("The path for $progname is not a file or program! This library was imported by ${progData.importingProgram}.")
                }
            }

            // Add program to seen programs and All progs.
            AllProgs.add(prog)
            seenPrograms[progImport] = progData
            addProgramImports(prog, progImport, obj.getPath(), seenPrograms, needToImportPrograms)
        }
    }

    fun addProgramImports(prog: Program, progImport: Import, progImportAbsPath: String, SeenPrograms: HashMap<Import, ImportProgramData>, needToImportPrograms: HashMap<Import, ImportProgramData>) {
        for (import in prog.imports) {
            if (!SeenPrograms.contains(import)) {
                needToImportPrograms[import] = ImportProgramData(import, progImport, progImportAbsPath)
            }
        }
    }
}