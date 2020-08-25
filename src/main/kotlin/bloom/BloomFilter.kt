/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

BloomFilter is used to test whether an element is a member of a set.
The returned results may contain false positives but no false negatives.
 */

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
            for (j in 0 until 5) {
                val item = Random.nextInt().toString().toByteArray()
                assert(hashes[i].digest(item) == summary.hashes[i].digest(item))
                { "Unable to apply merge, the randomness used are different" }
            }
        }

        // loop list and pick bigger
        summary.counters.forEachIndexed { index, element ->
            counters[index] = max(counters[index], element)
        }
    }
}