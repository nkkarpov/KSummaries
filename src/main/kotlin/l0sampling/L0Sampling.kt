package l0sampling

import sparserecovery.SparseRecovery
import java.security.MessageDigest
import kotlin.math.pow
import kotlin.random.Random

class L0Sampling {

    // number of sparse recovery
    var m: Int
    // store all sparse recovery
    val recoveries = emptyArray<SparseRecovery<Int>>().toMutableList()
    // compute probability to decide which level to go to
    val md = MessageDigest.getInstance("SHA-256")

    // sparse recovery initialize with n and l
    constructor(m: Int, n: Int, l: Int) {
        this.m = m
        for (i in 0 until m) {
            recoveries.add(SparseRecovery(n, l))
        }
    }

    constructor(m: Int, n: Int): this(m, n, 5)

    fun update(key: Int, weight: Double = 1.0) {
        val prob = 0.0
        for (i in 0 until m) {
            if (prob < 2.0.pow(i)) {
                recoveries[i].update(key, weight)
            } else {break}
        }
        return
    }

    fun query(): Int? {
        var res = emptyList<Int>()
        var flag = false
        for (i in 0 until m) {
            // If succeed at level i
            if (recoveries[i].query() != null) {
                res = recoveries[i].query()!!
                flag = true
                break
            }
        }
        if (flag) {
            res.toMutableList().shuffle()
            return res[0]
        }
        return null
    }

    fun merge(summary: L0Sampling) {
        // merge check
        assert(m == summary.m) { "Unable to apply merge, the number of sizes are not equal $m != ${summary.m}" }

        for (i in 0 until 10) {
            val item = Random.nextInt().toString().toByteArray()
            assert(md.digest(item) == summary.md.digest(item))
            { "Unable to apply merge, the randomness used are different" }
        }

        for (i in 0 until m) {
            recoveries[i].merge(summary.recoveries[i])
        }
    }
}