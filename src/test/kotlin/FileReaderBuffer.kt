package filereader

import java.io.BufferedReader
import java.io.File

class FileReaderBuffer {
    private var fileName: String
    private var buffer: BufferedReader

    constructor(fileName: String) {
        this.fileName = fileName
        buffer = File(fileName).bufferedReader()
    }

    fun next(): String? {
        val nextLine = buffer.readLine()
        if (nextLine != "\n") {
            return nextLine
        }
        else {
            return null
        }
    }
}