/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

Count-Min Sketch is used to estimate the item frequency.
This summary only accepts positive weights.
 */

package countmin

import saltedhash.SaltedHash
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

// counters as a d by t matrix
class CountMin<T> {
    private var counters: Array<Array<Double>>
    private var arrA: Array<Int>
    private var arrB: Array<Int>
    private val p = 7368787
    private var hash: SaltedHash
    private var d: Int
    private var t: Int

    constructor(d: Int, t: Int, intSeed: Int, hashSeed: Int) {
        this.d = d
        this.t = t
        arrA = Array(d, {0})
        arrB = Array(d, {0})
        counters = Array(d, { Array(t, {0.0})})

        // Initialize hash array
        val rd = Random(intSeed)
        for (i in 0 until d) {
            arrA[i] = rd.nextInt(p).absoluteValue
            arrB[i] = rd.nextInt(p).absoluteValue
        }
        hash = SaltedHash(1, hashSeed)
    }
    constructor(d: Int, t: Int): this(d, t, 10, 10)

    fun update (key: T, weight: Double) {
        val hash = getHash(key)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
            counters[i][index] += weight
        }
    }

    fun query (key: T): Double {
        var res = (10.0).pow(10)
        val hash = getHash(key)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
            res = min(res, counters[i][index])
        }
        return res
    }

    fun merge (summary: CountMin<T>) {
        assert(d == summary.d) { "Unable to apply merge, the size of rows is not equal $d != ${summary.d}" }
        assert(t == summary.t) { "Unable to apply merge, the size of cols is not equal $t != ${summary.t}" }
        assert(hash.mergeableWith(summary.hash)) {  "Unable to apply merge, different randomness applied!" }
        for (i in 0 until d) {
            assert(arrA[i] == summary.arrA[i]) {  "Unable to apply merge, different randomness applied!" }
            assert(arrB[i] == summary.arrB[i]) {  "Unable to apply merge, different randomness applied!" }
        }

        for (i in 0 until d) {
            for (j in 0 until t) {
                counters[i][j] = counters[i][j] + summary.counters[i][j]
            }
        }
    }

    private fun getIndex(hash: Int, index: Int) : Int {
        return (arrA[index] * hash + arrB[index]).rem(p).rem(t).absoluteValue
    }

    private fun getHash(key: String): Int {
        return hash.getHash(0, key)
    }
    private fun getHash(key: T) : Int {
        return hash.getHash(0, key.hashCode().toString())
    }
}