import org.junit.Test
import java.security.MessageDigest
import kotlin.math.absoluteValue


class HashTests {
    @Test
    fun testInit() {
        val numHash = 5
        val numMessage = 5

        val messages = arrayOf("hello", "hello", "will", "sh", "can")
        val hashes = mutableListOf<MessageDigest>()

        //Creating the MessageDigest object
        for (i in 0 until numHash) {
            var hash = MessageDigest.getInstance("SHA-256")
            hashes.add(hash)
        }

        //Compute the message digest
        for (i in 0 until numMessage) {
            for (j in 0 until numHash) {
                hashes[j].update(j.toString().toByteArray())
                val digest = hashes[j].digest(messages[i].toByteArray())
                println("Message: " + messages[i] + " on hash " + j)
                printHex(digest)
            }
        }
    }

    fun printHex(digest: ByteArray) {
        //Converting the byte array in to HexString format
        val hexString = StringBuffer()

        for (i in 0 until digest.size) {
            hexString.append(Integer.toHexString(0xFF and digest[i].toInt()))
        }
        println("Hex format : " + hexString.toString() + " Int format: " + getIndex(digest))
    }

    fun getIndex(digest: ByteArray) : Int {
        var result = 0
        var shift = 0
        for (i in 0 until 8) {
            result = result or (digest[i].toInt().absoluteValue shl shift)
            shift += 8
        }
        return result
    }
}