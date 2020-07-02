package hll

import java.security.MessageDigest
import kotlin.math.max
import kotlin.math.pow

class HyperLL<T> (val maxSize: Int) {

    // Private list / vector of size maxSize
    private val counters = mutableListOf<Int>(maxSize)
    // ? initialize with size and initial value 0

    private val mdIndex = MessageDigest.getInstance("SHA-256")
    private val mdZeros = MessageDigest.getInstance("SHA-256")

    private fun getIndex(bytes: ByteArray) : Int {
        val index = mdIndex.digest(bytes)
        return 0
    }

    private fun getLeadingZeros(bytes: ByteArray) : Int {
        val zeros = mdZeros.digest(bytes)
        return 0
    }

    fun update(key: T) {
        val bytes = key.hashCode().toString().toByteArray()
        val index = getIndex(bytes)
        val zeros = getLeadingZeros(bytes)

        if (zeros > counters[index]) counters[index] = zeros
    }

    fun query():Double {
        var total = 0.0
        val base = 2.0
        val alpha = 0.7213/ (1 + 1.079/maxSize.toDouble())

        counters.forEach{
            total += base.pow(-1*it)
        }

        return alpha * maxSize * maxSize / total
    }

    fun merge(summary: HyperLL<T>) {
        assert(maxSize == summary.maxSize) { "Unable to apply merge, the sizes are not equal $maxSize != ${summary.maxSize}" }
        // ?check hash function

        // loop vector and pick maximum
        summary.counters.forEachIndexed { index, element ->
            counters[index] = max(counters[index], element)
        }
    }
}

