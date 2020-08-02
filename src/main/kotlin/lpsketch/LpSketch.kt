package lpsketch

import java.util.*

class LpSketch<T> {
    var counters: Array<Double>
    var r = Random()
    var k: Int
    var p: Int

    constructor(p: Int, k: Int) {
        assert(p == 2) { "Currently do not support l_$p sampling" }

        this.p = p
        this.k = k

        counters = Array(this.k, {0.0})
    }

    fun update(key: Int, weight: Double = 1.0) {
        update(key.toDouble(), weight)
    }

    fun update(key: Double, weight: Double = 1.0) {
        for (i in 0 until k) {
            counters[i] += weight * p_stable(p) * key
        }
    }

    fun p_stable(p: Int): Double {
        if (p == 2) {
            return r.nextGaussian()
        }
        return 0.0
    }
}