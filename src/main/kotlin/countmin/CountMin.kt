package countmin

import java.security.MessageDigest
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random.Default.nextInt

class CountMin<T> (numRow: Int, numCol: Int) {
    private var counts = Array(numRow, {i -> Array(t, {j -> 0})})
    private var arrA = intArrayOf()
    private var arrB = intArrayOf()
    private val p = 7368787
    private val hashes = mutableListOf<MessageDigest>()
    private var d = 0
    private var t = 0

    init {
        // Get size
        d = numRow
        t = numCol
        /*
        counts = Array(d)
        for (i in 0 until d) {
            val arr = IntArray(t)
            counts[i] = arr
        }
         */

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
            // counts[i][index] += weight
        }
    }

    fun query (key: T) {
        var res = (10.0).pow(10).toInt()
        val hashcode = key.hashCode().toString().toByteArray()
        val hash = getHash(hashcode)
        for (i in 0 until d) {
            val index = getIndex(hash, i)
            // res = min(res, counts[i, index])
        }
    }

    fun merge (summary: CountMin<T>) {
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