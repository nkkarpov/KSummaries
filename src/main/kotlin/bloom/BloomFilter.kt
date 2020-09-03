/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

BloomFilter is used to test whether an element is a member of a set.
The returned results may contain false positives but no false negatives.
 */

package bloom

import saltedhash.SaltedHash
import kotlin.math.max

// k hash functions
class BloomFilter<T> (val maxSize: Int, val k: Int) {
    private val counters = mutableListOf<Boolean>()
    private val hashes: SaltedHash

    init {
        for (i in 0 until maxSize) {
            counters.add(false)
        }
        hashes = SaltedHash(k)
    }

    fun update(key: T) {
        val hashcode = key.hashCode()
        for (i in 0 until k) {
            val index = hashes.getIndex(i, hashcode.toString(), maxSize)
            counters[index] = true
        }
    }

    fun query(key: T): Boolean {
        val hashcode = key.hashCode()
        for (i in 0 until k) {
            val index = hashes.getIndex(i, hashcode.toString(), maxSize)
            if (!counters[index]) return false
        }
        return true
    }

    fun merge(summary: BloomFilter<T>) {
        assert(maxSize == summary.maxSize)
        { "Unable to apply merge, the sizes are not equal $maxSize != ${summary.maxSize}" }
        assert(k == summary.k)
        { "Unable to apply merge, the number of hash functions are not equal $k != ${summary.k}" }
        assert(hashes.mergeableWith(summary.hashes))
        { "Unable to apply merge, the randomness used are different" }

        // loop list and pick bigger
        summary.counters.forEachIndexed { index, element ->
            counters[index] = (counters[index] and element)
        }
    }
}