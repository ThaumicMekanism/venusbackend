package venusbackend.simulator

import venus.vfs.VFSFile

class FileDescriptor(var vfsFile: VFSFile, var fileMetaData: FileMetaData) {
    var feof = false
    var ferror = false
    val dataStream: StringBuilder = StringBuilder(vfsFile.readText())

    fun read(size: Int): String? {
        val amtToRead = minOf(size, dataStream.length - fileMetaData.readOffset)
        if (amtToRead == 0 || !fileMetaData.readable) {
            return null
        }
        val oldoffset = fileMetaData.readOffset
        fileMetaData.readOffset += amtToRead
        return dataStream.subSequence(oldoffset, fileMetaData.readOffset).toString()
    }

    fun write(value: String): Int? {
        if (!fileMetaData.writeable) {
            return null
        }
        dataStream.append(value)
        return value.length
    }

    fun flush(): Int {
        if (fileMetaData.writeable) {
            vfsFile.setText(this.dataStream.toString())
        }
        return 0
    }

    fun isEOF(): Int {
        return if (feof) {
            1
        } else {
            0
        }
    }

    fun getError(): Int {
        return if (ferror) {
            1
        } else {
            0
        }
    }
}

class FileMetaData(
    var readOffset: Int = 0,
    var readable: Boolean = false,
    var writeable: Boolean = false
)