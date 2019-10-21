package venusbackend.simulator

import venus.vfs.VFSFile
import venus.vfs.VFSType
// Fixme make the fileshandler handle numbers and not Ints!
class FilesHandler(sim: Simulator) {
    companion object {
        var EOF = -1
    }
    val files = HashMap<Int, FileDescriptor>()
    var fdCounter = 3

    fun openFile(sim: Simulator, filename: String, permissions: Int): Int {
        // open file in VFS here
        val o = sim.VFS.getObjectFromPath(filename)
        /*
        0 = r (only can read)
        1 = w (resets file contents and is only writable)
        2 = a (does not reset file contents but is only writeable)
        3 = r+ (RW)
        4 = w+ (RW reset file contents)
        5 = a+ (RW)
        */
        var resetText = false
        val rw = when (permissions) {
            0 -> {
                if (o == null) {
                    return EOF
                }
                FileMetaData(0, true, false)
            }
            1 -> {
                resetText = true
                FileMetaData(0, false, true)
            }
            2 -> {
                FileMetaData(0, false, true)
            }
            3 -> {
                if (o == null) {
                    return EOF
                }
                FileMetaData(0, true, true)
            }
            4 -> {
                resetText = true
                FileMetaData(0, true, true)
            }
            5 -> {
                FileMetaData(0, true, true)
            }
            else -> {
                return EOF
            }
        }
        val f = if (o == null) {
            sim.VFS.makeFileInDir(filename) ?: return EOF
        } else {
            if (o.type == VFSType.File) {
                o as VFSFile
            } else {
                return EOF
            }
        }
        if (resetText) {
            f.setText("")
        }
        val fd = FileDescriptor(f, rw)
        files[fdCounter] = fd
        return fdCounter++
    }

    fun getFileDescriptor(fdID: Int): FileDescriptor? {
        if (files.containsKey(fdID)) {
            return files[fdID]
        }
        return null
    }

    fun closeFileDescriptor(fdID: Int): Int {
        val fd = files.remove(fdID)
        return fd?.flush() ?: EOF
    }

    fun flushFileDescriptor(fdID: Int): Int {
        val fd = getFileDescriptor(fdID)
        return fd?.flush() ?: EOF
    }

    fun readFileDescriptor(fdID: Int, size: Int): String? {
        val fd = getFileDescriptor(fdID)
        return fd?.read(size)
    }

    fun writeFileDescriptor(fdID: Int, value: String): Int {
        val fd = getFileDescriptor(fdID)
        return fd?.write(value) ?: EOF
    }

    fun getFileDescriptorEOF(fdID: Int): Int {
        val fd = getFileDescriptor(fdID)
        return fd?.isEOF() ?: EOF
    }

    fun getFileDescriptorError(fdID: Int): Int {
        val fd = getFileDescriptor(fdID)
        return fd?.getError() ?: EOF
    }
}