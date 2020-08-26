import filereader.FileReaderBuffer
import org.junit.Test

class FileReaderBufferTests {
    @Test
    fun testInit() {
        val fileName = "data/T40I10D100K.txt"
        val reader = FileReaderBuffer(fileName)

        for (i in 0 until 10) {
            reader.next()
        }
        println(reader.next())
        println(reader.next())
        println(reader.next())
    }
}