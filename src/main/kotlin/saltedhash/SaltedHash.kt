package saltedhash

import java.security.MessageDigest
import kotlin.random.Random

class SaltedHash {
    private val hashes = mutableListOf<MessageDigest>()
    private val salts = mutableListOf<Int>()

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