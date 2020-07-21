package spacesaving

class SpaceSaving<T> (val maxSize: Int) {
    private var counters = DoubleArray(maxSize)
    private val keys = mutableListOf<T>()
    private var pointer = 0

    init {
        counters = DoubleArray(maxSize)
        for (i in 0 until maxSize) {
            counters[i] = 0.0
        }
    }

    fun update(key: T, weight: Double = 1.0) {
        if (pointer == 0) {
            counters[0] = weight
            keys.add(key)
        }

        for (i in 0 until pointer) {
            if (key == keys[pointer]) {
                counters[i] += weight
                return
            }
        }

        if (pointer < maxSize) {
            keys.add(key)
            counters[pointer] = weight
            pointer += 1
            return
        }

        purge(key, weight)
    }

    fun query(key: T): Double {
        for (i in 0 until maxSize) {
            if (key == keys[i]) {
                return counters[i]
            }
        }
        return 0.0
    }

    private fun purge(key: T, weight: Double) {
        var minimum = counters[0]
        var index = 0
        for (i in 0 until maxSize) {
            if (counters[i] < minimum) {
                minimum = counters[i]
                index = i
            }
        }
        counters[index] += weight
        keys[index] = key
    }

    fun merge(summary: SpaceSaving<T>) {
        assert(maxSize == summary.maxSize) { "Unable to apply merge, the sizes are not equal $maxSize != ${summary.maxSize}" }
        for (i in 0 until maxSize) {
            update(summary.keys[i], summary.counters[i])
        }
    }
}