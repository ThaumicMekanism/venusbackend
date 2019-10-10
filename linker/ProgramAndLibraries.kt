package venusbackend.linker

import venusbackend.assembler.AssemblerError
import venus.Driver.assemble
import venus.vfs.VFSFile
import venus.vfs.VFSProgram
import venus.vfs.VFSType
import venus.vfs.VirtualFileSystem
import venusbackend.riscv.Program

class ProgramAndLibraries(val progs: List<Program>, vfs: VirtualFileSystem) {
    val AllProgs = ArrayList<Program>()
    init {
        val seenPrograms = HashMap<String, ImportProgramData>()
        val needToImportPrograms = HashMap<String, ImportProgramData>()
        val mainProg = "the main program"
        for (prog in progs) {
            seenPrograms[prog.name] = ImportProgramData(prog.name, mainProg)
            addProgramImports(prog, mainProg, seenPrograms, needToImportPrograms)
            AllProgs.add(prog)
        }
        val keysToRemove = HashSet<String>()
        for (sprog in seenPrograms.values) {
            if (needToImportPrograms.containsKey(sprog.progPath)) {
                keysToRemove.add(sprog.progPath)
            }
        }
        for (ktr in keysToRemove) {
            needToImportPrograms.remove(ktr)
        }
        while (needToImportPrograms.isNotEmpty()) {
            val progname = needToImportPrograms.keys.elementAt(0)
            val progData = needToImportPrograms[progname]!!
            needToImportPrograms.remove(progname)
            // Get the file
            val obj = vfs.getObjectFromPath(progname) ?: throw AssemblerError("Could not find the library: $progname. This library was imported by ${progData.importingProgram}.")

            val prog = when (obj.type) {
                VFSType.File -> {
                    // Get the text
                    val progText = (obj as VFSFile).readText()
                    val p = assemble(progText) ?: throw AssemblerError("Could not load the library: $progname (Note: The library file was found but it could not be assembled) This library was imported by ${progData.importingProgram}.")
                    p.name = progname
                    p
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
            seenPrograms[progname] = progData
            addProgramImports(prog, progname, seenPrograms, needToImportPrograms)
        }
    }

    fun addProgramImports(prog: Program, progname: String, SeenPrograms: HashMap<String, ImportProgramData>, needToImportPrograms: HashMap<String, ImportProgramData>) {
        for (import in prog.imports) {
            if (!SeenPrograms.contains(import)) {
                needToImportPrograms[import] = ImportProgramData(import, progname)
            }
        }
    }
}