package l0sampling

import saltedhash.SaltedHash
import sparserecovery.SparseRecovery
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.random.Random

class L0Sampling {

    // number of sparse recovery
    var m: Int
    // store all sparse recovery
    val recoveries = emptyArray<SparseRecovery<Int>>().toMutableList()
    // compute probability to decide which level to go to
    val hash: SaltedHash
    // Store the results if the sampler has been queried
    var tempResults = emptyList<Int>().toMutableList()
    // if the sampler has been queried
    var queried = false

    // sparse recovery initialize with n and l
    // bloomFilter initialize with maxSize and k
    constructor(m: Int, n: Int, l: Int, maxSize: Int, k: Int, hashSeed: Int = 100) {
        this.m = m
        for (i in 0 until m) {
            recoveries.add(SparseRecovery(n, l, maxSize, k, hashSeed))
        }
        hash = SaltedHash(1, hashSeed)
    }

    // sparse recovery initialize with n and l
    constructor(m: Int, n: Int, l: Int): this(m, n, l, n, l)
    constructor(m: Int, n: Int): this(m, n, 5)

    // m levels of possibilities
    fun update(key: Int, weight: Double = 1.0) {
        val prob = hash.getIndex(0, key.toString(), 2.0.pow(m-1).toInt())
        for (i in 0 until m) {
            if (prob < 2.0.pow(m-1-i).toInt()) {
                recoveries[i].update(key, weight)
            } else {break}
        }
        return
    }

    fun query(): Int? {
        var res = tempResults
        if (! queried)
            res = queryAll().toMutableList()
        if (res.isNotEmpty()) {
//            println(res)
            res.shuffle()
            return res[0]
        }
        return null
    }

    private fun queryAll(): List<Int> {
        var res = emptyList<Int>()
        for (i in 0 until m) {
            // If succeed at level i
            val recovery = recoveries[i].query()
            if (recovery != null) {
                res = recovery
                break
            }
        }

        // Change queried flag to true
        queried = true
        // Store current results to tempResults
        for (i in res.indices) {
            tempResults.add(res[i])
        }
        tempResults.toList()

        return res
    }

    fun merge(summary: L0Sampling) {
        // merge check
        assert(m == summary.m) { "Unable to apply merge, the number of sizes are not equal $m != ${summary.m}" }
//        assert(!(queried or summary.queried)) { "Unable to apply merge, queried samplers are not able to merge" }

        for (i in 0 until m) {
            recoveries[i].merge(summary.recoveries[i])
        }
        queried = false
        tempResults.clear()
    }
}