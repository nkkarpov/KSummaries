package conservativesketch

import java.security.MessageDigest
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class ConservativeSketch<T> (val d: Int, val t: Int) {
    private var counters = Array(d, { Array(t, {0.0})})
    private var arrA = Array(d, {0})
    private var arrB = Array(d, {0})
    private val p = 7368787
    private val md = MessageDigest.getInstance("SHA-256")

    init {
        // Initialize hash functions
        for (i in 0 until d) {
            arrA[i] = Random.nextInt(p)
            arrB[i] = Random.nextInt(p)
        }
    }

    fun update (key: T, weight: Double) {
        val hash = getHash(key)
        var indexMin = 0
        var iMin = 0
        var countMin = (10.0).pow(10)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
            val count = counters[i][index]
            if (count < countMin) {
                countMin = count
                iMin = i
                indexMin = index
            }
        }
        counters[iMin][indexMin] += weight
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

    // Approximation guarantee will change after merge
    fun merge (summary: ConservativeSketch<T>) {
        assert(d == summary.d) { "Unable to apply merge, the size of rows is not equal $d != ${summary.d}" }
        assert(t == summary.t) { "Unable to apply merge, the size of cols is not equal $t != ${summary.t}" }
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