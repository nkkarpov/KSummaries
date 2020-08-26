package filereader

import java.io.BufferedReader
import java.io.File

class FileReaderBuffer {
    private lateinit var fileName: String
    private lateinit var buffer: BufferedReader

    constructor(fileName: String) {
        println(fileName)
        this.fileName = fileName
        buffer = File(fileName).bufferedReader()
    }

    fun next(): String? {
        val nextLine = buffer.readLine()
        if (nextLine != "\n")
            return nextLine
        else
            return null
    }
}