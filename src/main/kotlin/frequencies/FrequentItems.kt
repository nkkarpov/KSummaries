package frequencies

import misc.Counter


class FrequentItems<T>(val maxSize: Int) {
    private val counters = emptyList<Counter<T, Double>>().toMutableList()
    fun estimate(key: T) = counters.firstOrNull { it.key == key }?.value ?: 0.0
    fun frequentItems(): List<Counter<T, Double>> = counters.toList()

    fun update(key: T, increment: Double = 1.0) {
        var flag = false
        counters.forEach {
            if (it.key == key) {
                it.value += increment
                flag = true
            }
        }
        if (!flag) counters.add(Counter(key, increment))
        if (counters.size > maxSize) purge()
    }

    private fun purge() {
        val index = counters.withIndex().minBy { it.value.value }?.index!!
        val decrement = counters[index].value
        counters.removeAt(index)
        counters.forEach { it.value -= decrement }
    }

    fun merge(summary: FrequentItems<T>) {
        assert(maxSize == summary.maxSize) { "Unable to apply merge, the sizes are not equal $maxSize != ${summary.maxSize}" }
        summary.frequentItems().forEach { update(it.key, it.value) }
    }
}