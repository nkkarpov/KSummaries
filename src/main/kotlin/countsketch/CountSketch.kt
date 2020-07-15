package countsketch

import java.security.MessageDigest
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.random.Random

class CountSketch<T> (val d: Int, val t: Int) {
    private var counters = Array(d, { Array(t, {0.0})})
    private var arrA = Array(d, {0})
    private var arrB = Array(d, {0})
    private var arrC = Array(d, {0})
    private var arrD = Array(d, {0})
    private val p = 7368787
    private val md = MessageDigest.getInstance("SHA-256")

    init {
        // Initialize hash functions
        for (i in 0 until d) {
            arrA[i] = Random.nextInt(p)
            arrB[i] = Random.nextInt(p)
            arrC[i] = Random.nextInt(p)
            arrD[i] = Random.nextInt(p)
        }
    }

    fun update (key: T, weight: Double) {
        val hash = getHash(key)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
            val sign = getSign(hash, i)
            counters[i][index] += weight*sign
        }
    }

    fun query (key: T): Double {
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
    private fun getIndex(hash: Int, index: Int) : Int {
        val sign = (arrA[index] * hash + arrB[index]).rem(p).rem(t)
        assert(sign == -1 || sign == 1) { "hash implementation error! "}
        return sign
    }

    // Obtain the sign
    private fun getSign(hash: Int, index: Int) : Int {
        return 2*((arrC[index] * hash + arrD[index]).rem(p).rem(2))-1
    }

    // Item to int hash value
    private fun getHash(key: T) : Int {
        val bytes = md.digest(key.hashCode().toString().toByteArray())
        var result = 0
        var shift = 0
        for (i in 0 until 8) {
            result = result or (bytes.get(i).toInt() shl shift)
            shift += 8
        }
        return result.absoluteValue.rem(p)
    }
}