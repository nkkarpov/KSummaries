package saltedhash

import java.security.MessageDigest
import kotlin.math.absoluteValue
import kotlin.random.Random

class SaltedHash {
    private val md = MessageDigest.getInstance("SHA-256")
    private val salts = mutableListOf<Int>()
    private val numHash: Int

    // Input number of hash functions
    constructor(numHash: Int, seed: Int = 100) {
        this.numHash = numHash
        val rd = Random(seed)

        for (i in 0 until numHash) {
            // Initialize salts
            val salt = rd.nextInt()
            salts.add(salt)
        }
    }

    // Input message, output int hash value
    // Feed with salts[i] first and then hash
    fun getHash(index: Int, message: String): Int {
        // Add corresponding salt then hash
        md.update(salts[index].toByte())
        val digest = md.digest(message.toByteArray())

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

    fun numHash():Int {return numHash}

    fun mergeableWith(saltedHash: SaltedHash): Boolean{
        // Check salts
        for (i in 0 until salts.size) {
            if (salts[i] != saltedHash.salts[i]) return false
        }

        return true
    }
}