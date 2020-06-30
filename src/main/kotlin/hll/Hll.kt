package hll

import kotlin.math.pow

class HyperLL<T> (val maxSize: Int) {

    // Private list / vector of size maxSize
    private val counters = mutableListOf<Int>()

    fun update(key: T) {
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
        // loop vector and pick maximum
    }
}

