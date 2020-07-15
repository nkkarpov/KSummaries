package countmin

import misc.Counter
import java.security.MessageDigest
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random.Default.nextInt

class CountMin<T> (val d: Int, val t: Int) {
    private var counters = MutableList<MutableList<Double>>()
    private var arrA = intArrayOf()
    private var arrB = intArrayOf()
    private val p = 7368787
    private val hashes = mutableListOf<MessageDigest>()

    init {
        // Get size
        for (i in 0 until d) {
            var list = MutableList<Double>()
            for (j in 0 until t) {
                list.add(0.0)
            }
            counters.add(list)
        }

        // Initialize hash functions
        for (i in 0 until d) {
            var hash = MessageDigest.getInstance("SHA-256")
            hashes.add(hash)
        }

        // Initialize array
        arrA = intArrayOf(d)
        arrB = intArrayOf(d)
        for (i in 0 until d) {
            arrA[i] = nextInt(p)
            arrB[i] = nextInt(p)
        }
    }

    fun update (key: T, weight: Double) {
        val hashcode = key.hashCode().toString().toByteArray()
        val hash = getHash(hashcode)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
             counters[i][index] += weight
        }
    }

    fun query (key: T) {
        var res = (10.0).pow(10)
        val hashcode = key.hashCode().toString().toByteArray()
        val hash = getHash(hashcode)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
             res = min(res, counters[i][index])
        }
    }

    fun merge (summary: CountMin<T>) {
        assert(d == summary.d) { "Unable to apply merge, the size of rows is not equal $d != ${summary.d}" }
        assert(t == summary.t) { "Unable to apply merge, the size of cols is not equal $t != ${summary.t}" }
        for (i in 0 until d) {
            for (j in 0 until t) {
                counters[i][j] = min(counters[i][j], summary.counters[i][j])
            }
        }
    }

    private fun getIndex(hash: Int, index: Int) : Int {
        return (arrA[index] * hash + arrB[index]).rem(p).rem(t)
    }

    private fun getHash(bytes: ByteArray) : Int {
        var result = 0
        var shift = 0
        for (i in 0 until 8) {
            result = result or (bytes.get(i).toInt() shl shift)
            shift += 8
        }
        return result
    }
}