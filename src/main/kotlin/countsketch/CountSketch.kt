/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

Count Sketch is used to estimate the item frequency.
This summary can accept both positive and negative weights.
 */

package countsketch

import saltedhash.SaltedHash
import kotlin.math.absoluteValue
import kotlin.random.Random

open class CountSketch<T> {
    var counters: Array<Array<Double>>
    private var arrA: Array<Int>
    private var arrB: Array<Int>
    private var arrC: Array<Int>
    private var arrD: Array<Int>
    // array sizes
    var d: Int
    var t: Int

    private val p = 7368787
    private val hash: SaltedHash

    constructor(d: Int, t: Int, intSeed: Int, hashSeed: Int) {
        counters = Array(d, { Array(t, {0.0})})
        arrA = Array(d, {0})
        arrB = Array(d, {0})
        arrC = Array(d, {0})
        arrD = Array(d, {0})

        this.d = d
        this.t = t

        hash = SaltedHash(1, hashSeed)

        // Initialize hash array
        val rd = Random(intSeed)
        for (i in 0 until d) {
            arrA[i] = rd.nextInt(p).absoluteValue
            arrB[i] = rd.nextInt(p).absoluteValue
            arrC[i] = rd.nextInt(p).absoluteValue
            arrD[i] = rd.nextInt(p).absoluteValue
        }
    }
    constructor(d: Int, t: Int): this(d, t, 20, 20)

    fun update (key: T, weight: Double) {
        val hash = getHash(key)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
            val sign = getSign(hash, i)
            counters[i][index] += weight*sign
        }
    }

    open fun query (key: T): Double {
        var res = DoubleArray(d, {0.0})
        val hash = getHash(key)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
            val sign = getSign(hash, i)
            res[i] = sign * counters[i][index]
        }
        res.sort()
        return res[d/2]
    }

    fun merge (summary: CountSketch<T>) {
        assert(d == summary.d && t == summary.t)
        { "Unable to apply merge, the size of rows is not equal $d != ${summary.d}" }
        assert(hash.mergeableWith(summary.hash))
        {  "Unable to apply merge, different randomness applied!" }
        for (i in 0 until d) {
            assert(arrA[i] == summary.arrA[i] && arrB[i] == summary.arrB[i]
                    && arrC[i] == summary.arrC[i] && arrD[i] == summary.arrD[i])
            {  "Unable to apply merge, different randomness applied!" }
        }

        for (i in 0 until d) {
            for (j in 0 until t) {
                counters[i][j] = counters[i][j] + summary.counters[i][j]
            }
        }
    }

    // Obtain the col index for a given row
    fun getIndex(hash: Int, index: Int) : Int {
        return (arrA[index] * hash + arrB[index]).absoluteValue.rem(p).rem(t)
    }

    // Obtain the sign
    fun getSign(hash: Int, index: Int) : Int {
        val sign =  2*((arrC[index] * hash + arrD[index]).absoluteValue.rem(p).rem(2))-1
        assert(sign == -1 || sign == 1) { "hash implementation error! "}
        return sign
    }

    fun getHash(key: String) : Int {
        return hash.getHash(0, key.toString())
    }
    // Item to int hash value
    fun getHash(key: T) : Int {
        return hash.getHash(0, key.hashCode().toString())
    }
}