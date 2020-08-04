package bloom

import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

class BloomFilter<T> (val maxSize: Int, val k: Int) {
    private val counters = mutableListOf<Int>()
    private val hashes = mutableListOf<MessageDigest>()

    init {
        for (i in 0 until maxSize) {
            counters.add(0)
        }
        for (i in 0 until k) {
            var hash = MessageDigest.getInstance("SHA-256")
            hashes.add(hash)
        }
    }

    private fun getIndex(bytes: ByteArray) : Int {
        var result = 0
        var shift = 0
        for (i in 0 until 8) {
            result = result or (bytes.get(i).toInt() shl shift)
            shift += 8
        }
        return abs(result).rem(maxSize)
    }

    fun update(key: T) {
        val hashcode = key.hashCode()
        for (i in 0 until k) {
            val bytes = hashes[i].digest(hashcode.toString().toByteArray())
            val index = getIndex(bytes)
            counters[index] = 1
        }
    }

    fun query(key: T): Boolean {
        val hashcode = key.hashCode()
        for (i in 0 until k) {
            val bytes = hashes[i].digest(hashcode.toString().toByteArray())
            val index = getIndex(bytes)
            if (counters[index] != 1) return false
        }
        return true
    }

    fun merge(summary: BloomFilter<T>) {
        assert(maxSize == summary.maxSize) { "Unable to apply merge, the sizes are not equal $maxSize != ${summary.maxSize}" }
        assert(k == summary.k) { "Unable to apply merge, the number of hash functions are not equal $k != ${summary.k}" }

        for (i in 0 until k) {
            for (j in 0 until 10) {
                assert(hashes[i].digest(Random.nextInt().toString().toByteArray())
            == summary.hashes[i].digest(Random.nextInt().toString().toByteArray()))
                { "Unable to apply merge, the randomness used are different" }
            }
        }

        // loop list and pick bigger
        summary.counters.forEachIndexed { index, element ->
            counters[index] = max(counters[index], element)
        }
    }
}