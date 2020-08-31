package saltedhash

import java.security.MessageDigest
import kotlin.math.absoluteValue
import kotlin.random.Random

class SaltedHash {
    private val hashes = mutableListOf<MessageDigest>()
    private val salts = mutableListOf<Int>()

    // Input number of hash functions
    constructor(numHash: Int, seed: Int = 100) {
        val rd = Random(seed)

        for (i in 0 until numHash) {
            // Initialize hash functions
            val md = MessageDigest.getInstance("SHA-256")
            hashes.add(md)

            // Initialize salts
            val salt = rd.nextInt()
            salts.add(salt)
        }
    }

    // Input message, output int hash value
    // Returned by the hashes[index]
    fun getHash(index: Int, message: String): Int {
        // Add corresponding salt
        hashes[index].update(salts[index].toByte())
        val digest = hashes[index].digest(message.toByteArray())

        // Convert digest[0-7] to a positive integer
        var result = 0
        var shift = 0
        for (i in 0 until 8) {
            result = result or (digest[i].toInt() shl shift).absoluteValue
            shift += 8
        }
        return result.absoluteValue
    }

    // Returned by the hashes[index]
    // With additional maxRange parameter to get an index
    fun getIndex(index: Int, message: String, maxRange: Int): Int {
        if (maxRange > 0)
            return getHash(index, message).rem(maxRange)
        else
            error("Max range cannot be negative")
    }

    fun mergeableWith(saltedHash: SaltedHash): Boolean{
        // Check hash function size
        if (hashes.size != saltedHash.hashes.size) return false

        // Check salts
        for (i in 0 until hashes.size) {
            if (salts[i] != saltedHash.salts[i]) return false
        }

        return true
    }
}