 package venusbackend.riscv.insts.integer.base.i.ecall

import venus.Renderer
import venusbackend.compareTo
import venusbackend.inc
import venusbackend.numbers.QuadWord
import venusbackend.numbers.toQuadWord
import venusbackend.riscv.InstructionField
import venusbackend.riscv.Registers
import venusbackend.riscv.insts.dsl.types.Instruction
import venusbackend.riscv.insts.dsl.disasms.RawDisassembler
import venusbackend.riscv.insts.dsl.formats.FieldEqual
import venusbackend.riscv.insts.dsl.formats.InstructionFormat
import venusbackend.riscv.insts.dsl.impls.NoImplementation
import venusbackend.riscv.insts.dsl.impls.RawImplementation
import venusbackend.riscv.insts.dsl.parsers.DoNothingParser
import venusbackend.riscv.MemorySegments
import venusbackend.simulator.FilesHandler
import venusbackend.simulator.Simulator
import kotlin.js.JSON
import kotlin.js.Json
import kotlin.js.json

val ecall = Instruction(
    // Fixme The long and quadword are only build for a 32 bit system!
        name = "ecall",
        format = InstructionFormat(4,
                listOf(FieldEqual(InstructionField.ENTIRE, 0b000000000000_00000_000_00000_1110011))
        ),
        parser = DoNothingParser,
        impl16 = NoImplementation,
        impl32 = RawImplementation { mcode, sim ->
            val whichCall = sim.getReg(Registers.a0)
            when (whichCall) {
                1 -> printInteger(sim)
                4 -> printString(sim)
                5 -> atoi(sim)
                9 -> sbrk(sim)
                10 -> exit(sim)
                11 -> printChar(sim)
                13 -> openFile(sim)
                14 -> readFile(sim)
                15 -> writeFile(sim)
                16 -> closeFile(sim)
                17 -> exitWithCode(sim)
                18 -> fflush(sim)
                19 -> feof(sim)
                20 -> ferror(sim)
                34 -> printHex(sim)
                0x3CC -> clib(sim)
                else -> invokeECall(whichCall.toInt(), sim)
            }
            if (!(whichCall == 10 || whichCall == 17)) {
                sim.incrementPC(mcode.length)
            }
        },
        impl64 = RawImplementation { mcode, sim ->
            val whichCall = sim.getReg(10).toLong()
            when (whichCall) {
                1L -> printInteger(sim)
                4L -> printString(sim)
                5L -> atoi(sim)
                9L -> sbrk(sim)
                10L -> exit(sim)
                11L -> printChar(sim)
                13L -> openFile(sim)
                14L -> readFile(sim)
                15L -> writeFile(sim)
                16L -> closeFile(sim)
                17L -> exitWithCode(sim)
                18L -> fflush(sim)
                19L -> feof(sim)
                20L -> ferror(sim)
                34L -> printHex(sim)
                else -> invokeECall(whichCall.toInt(), sim)
            }
            if (!(whichCall == 10L || whichCall == 17L)) {
                sim.incrementPC(mcode.length)
            }
        },
        impl128 = RawImplementation { mcode, sim ->
            val whichCall = sim.getReg(10).toQuadWord()
            when (whichCall) {
                QuadWord(1) -> printInteger(sim)
                QuadWord(4) -> printString(sim)
                QuadWord(5) -> atoi(sim)
                QuadWord(9) -> sbrk(sim)
                QuadWord(10) -> exit(sim)
                QuadWord(11) -> printChar(sim)
                QuadWord(13) -> openFile(sim)
                QuadWord(14) -> readFile(sim)
                QuadWord(15) -> writeFile(sim)
                QuadWord(16) -> closeFile(sim)
                QuadWord(17) -> exitWithCode(sim)
                QuadWord(18) -> fflush(sim)
                QuadWord(19) -> feof(sim)
                QuadWord(20) -> ferror(sim)
                QuadWord(34) -> printHex(sim)
                else -> invokeECall(whichCall.toInt(), sim)
            }
            if (!(whichCall == QuadWord(10) || whichCall == QuadWord(17))) {
                sim.incrementPC(mcode.length)
            }
        },
        disasm = RawDisassembler { "ecall" }
)

enum class Syscall(val syscall: Int) {
    PRINT_INT(1),
    PRINT_STR(4),
    ATOI(5),
    SBRK(9),
    EXIT(10),
    PRINT_CHAR(11),
    OPEN(13),
    READ(14),
    WRITE(15),
    CLOSE(16),
    EXIT_WITH_CODE(17),
    FLUSH(18),
    FEOF(19),
    FERROR(20),
    PRINT_HEX(34)
}

private fun invokeECall(id: Int, sim: Simulator) {
    if (!sim.invokeECallReceiver(id)) Renderer.printConsole("Invalid ecall $id")
}


// All file operations will return -1 if the file descriptor is not found.
private fun openFile(sim: Simulator) {
    /**
     * Attempt to open the file with the lowest number to return first. If cannot open file, return -1.
     * Look here for the permissionbits:https://en.cppreference.com/w/c/io/fopen
     *
     * a0=13,a1=filename,a2=permissionbits -> a0=filedescriptor
     */
    val filenameAddress = sim.getReg(Registers.a1).toInt()
    val permissions = sim.getReg(Registers.a2).toInt()
    val filename = getString(sim, filenameAddress)
    val fdID = sim.filesHandler.openFile(sim, filename, permissions)
    sim.setReg(Registers.a0, fdID)
}

private fun readFile(sim: Simulator) {
    /**
     * Check file descriptor and check if we have the valid permissions.
     * If we can read from the file, start reading at the offset (default=0)
     * and increment the offset by the bytes read. Return the number of bytes which were read.
     * User will call feof(fd) or ferror(fd) for if the output is not equal to the length.
     *
     * a0=14, a1=filedescriptor, a2=where to store data, a3=amt to read -> a0=Number of items read
     */
    val fdID = sim.getReg(Registers.a1).toInt()
    val bufferAddress = sim.getReg(Registers.a2).toInt()
    val size = sim.getReg(Registers.a3).toInt()
    if (size < 0) {
        sim.setReg(Registers.a0, FilesHandler.EOF)
        return
    }
    val data = sim.filesHandler.readFileDescriptor(fdID, size)
    var offset = 0
    if (data != null) {
        for (c in data) {
            sim.storeBytewCache(bufferAddress + offset, c.toInt())
            offset++
        }
        sim.setReg(Registers.a0, offset)
    } else {
        sim.setReg(Registers.a0, FilesHandler.EOF)
    }
}

private fun writeFile(sim: Simulator) {
    /**
     * a0=15, a1=filedescriptor, a2=buffer to read data, a3=amt to write, a4=size of each item -> a0=Number of items written
     */
    // FIXME This needs to be redone to handle different possible errors for results. Currently this is fine due to the error which can be produced.
    val fdID = sim.getReg(Registers.a1).toInt()
    val bufferAddress = sim.getReg(Registers.a2).toInt()
    val sizeOfItem = sim.getReg(Registers.a4).toInt()
    val size = sim.getReg(Registers.a3).toInt() * sizeOfItem
    if (size < 0) {
        sim.setReg(Registers.a0, FilesHandler.EOF)
        return
    }
    var offset = 0
    val sb = StringBuilder()
    while (offset < size) {
        var addr = bufferAddress + offset
        var byte = sim.loadByte(addr).toShort()
        var char = byte.toChar()
        sb.append(char)
        offset++
    }
    val s = sb.toString()
    var result = sim.filesHandler.writeFileDescriptor(fdID, s)
    if (result != FilesHandler.EOF) {
        result /= sizeOfItem
    }
    sim.setReg(Registers.a0, result)
}

private fun seekFile(sim: Simulator) {
//
}

private fun tellFile(sim: Simulator) {
//
}

private fun closeFile(sim: Simulator) {
    /**
     * Flush the data written to the file back to where it came from.
     * a0=16, a1=filedescriptor -> ​0​ on success, EOF otherwise
     */
    val fdID = sim.getReg(Registers.a1).toInt()
    val a0 = sim.filesHandler.closeFileDescriptor(fdID)
    sim.setReg(Registers.a0, a0)
}

private fun fflush(sim: Simulator) {
    /**
     * Returns zero on success. Otherwise EOF is returned and the error indicator of the file stream is set.
     * a0=19, a1=filedescriptor -> a0=if end of file
     */
    val fdID = sim.getReg(Registers.a1).toInt()
    val a0 = sim.filesHandler.flushFileDescriptor(fdID)
    sim.setReg(Registers.a0, a0)
}

private fun feof(sim: Simulator) {
    /**
     * Will return nonzero value if the end of the stream has been reached, otherwise ​0​
     *
     * a0=19, a1=filedescriptor -> a0=if end of file
     */
    val fdID = sim.getReg(Registers.a1).toInt()
    val a0 = sim.filesHandler.getFileDescriptorEOF(fdID)
    sim.setReg(Registers.a0, a0)
}

private fun ferror(sim: Simulator) {
    /**
     * Will return Nonzero value if the file stream has errors occurred, ​0​ otherwise
     *
     * a0=20, a1=filedescriptor -> a0=if error occured
     */
    val fdID = sim.getReg(Registers.a1).toInt()
    val a0 = sim.filesHandler.getFileDescriptorError(fdID)
    sim.setReg(Registers.a0, a0)
}

private fun printHex(sim: Simulator) {
    val arg = sim.getReg(11)
    sim.ecallMsg = Renderer.toHex(arg)
    Renderer.printConsole(sim.ecallMsg)
}

private fun printInteger(sim: Simulator) {
    val arg = sim.getReg(11)
    sim.ecallMsg = arg.toString()
    Renderer.printConsole(sim.ecallMsg)
}

private fun printString(sim: Simulator) {
    val arg = sim.getReg(11)
    val s = getString(sim, arg)
    sim.ecallMsg += s
    Renderer.printConsole(s)
}

private fun atoi(sim: Simulator) {
    val str_pointer = sim.getReg(Registers.a1)
    val s = getString(sim, str_pointer)
    val n = try {
        s.toInt()
    } catch (e: NumberFormatException) {
        0
    }
    sim.setReg(Registers.a0, n)
}

private fun sbrk(sim: Simulator) {
    val bytes = sim.getReg(11)
    if (bytes < 0) return
    sim.setReg(10, sim.getHeapEnd())
    sim.addHeapSpace(bytes)
}

private fun exit(sim: Simulator) {
    sim.setPC(MemorySegments.STATIC_BEGIN)
    sim.exitcode = 0
    // sim.ecallMsg = "exiting the simulator"
}

private fun printChar(sim: Simulator) {
    val arg = sim.getReg(11)
    sim.ecallMsg = (arg.toChar()).toString()
    Renderer.printConsole(arg.toChar())
}

private fun exitWithCode(sim: Simulator) {
    sim.setPC(MemorySegments.STATIC_BEGIN)
    val retVal = sim.getReg(11)
    sim.exitcode = retVal.toInt()
    sim.ecallMsg = "\nExited with error code $retVal"
    Renderer.printConsole("\nExited with error code $retVal\n")
}

private fun memdump(sim: Simulator) {
    /**
     * Dumps Venus's state to the filename given as a pointer in a1. If a1 points to 0,
     * then the name is set to "venus.dump"
     *
     * a0=22, a1=(optional)Filename pointer
     */
    val filepathptr = sim.getReg(Registers.a1)
    val filepath = if (filepathptr == 0) {
        "venus.dump"
    } else {
        getString(sim, filepathptr)
    }
    // TODO Complete me
    var err = sim.VFS.touch(filepath)
    if (err == "") {
        err = sim.VFS.write(filepath, "")
    }
}

private fun getString(sim: Simulator, address: Number): String {
    var addr = address
    val s = StringBuilder()
    var c = sim.loadByte(address)
    addr++
    while (c != 0) {
        s.append(c.toChar())
        c = sim.loadByte(addr)
        addr++
    }
    return s.toString()
}
