package lpsketch

import java.util.*

class LpSketch<T> {
    var counters: Array<Double>
    var r = Random()
    var k: Int
    var p: Int
    val big = r.nextInt()*7368787
    var numUpdate = 0

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
        numUpdate += 1
    }

    fun p_stable(p: Int): Double {
        if (p == 2) {
            return r.nextGaussian()
        }
        return 0.0
    }

    fun query(): Double {
        val res = Array<Double>(k, {0.0})
        for (i in 0 until k) {
            res[i] = counters[i]
        }
        res.sort()
        return res[k/2]
    }

    fun merge(summary: LpSketch<T>) {
        assert(big == summary.big && k == summary.k && p == summary.p)
        { "Unable to apply merge, the randomness does not match" }

        for (i in 0 until k) {
            counters[i] += summary.counters[i]
        }
    }

}