package sparserecovery

import bloom.BloomFilter
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random

class Cell {
    var count = 0.0
    var sum = 0.0
    var fingerprint: BloomFilter<Int>

    constructor(maxSize: Int, k: Int) {
        fingerprint = BloomFilter<Int>(maxSize, k)
    }

    fun merge(summary: Cell) {
        count += summary.count
        sum += summary.sum
        fingerprint.merge(summary.fingerprint)
    }
}

class SparseRecovery<T> {
    // n counters
    private val counters = emptyArray<Cell>().toMutableList()
    private var n: Int
    // l hash functions
    private val hashes = emptyArray<MessageDigest>().toMutableList()
    private var l: Int
    // parameters for bloom filters
    private var maxSize: Int
    private var k: Int
    // threshold in division
    private val threshold = 0.000001

    constructor(n: Int, l: Int): this(n,l,n,l)

    constructor(n: Int, l: Int, maxSize: Int, k: Int) {
        this.n = if (n > 0) n else error("parameter is negative")
        this.l = if (l > 0) l else error("parameter is negative")
        this.maxSize = if (maxSize > 0) maxSize else error("parameter is negative")
        this.k = if (k > 0) k else error("parameter is negative")

        for (i in 0 until this.n) {
            counters.add(Cell(maxSize, k))
        }
        for (i in 0 until this.l) {
            hashes.add(MessageDigest.getInstance("SHA-256"))
        }
    }

    fun update(key: Int, weight: Double = 1.0) {
        val set = emptySet<Int>().toMutableSet()
//        println("Insert " + key)
        for (i in 0 until l) {
            val hash = hashes[i].digest(key.toString().toByteArray())
            val index = getIndex(hash, n)
            if (set.isEmpty() || (!set.contains(index))) {
                set.add(index)
//                println("item " + key + " in cell " + index)
                counters[index].count += weight
                counters[index].sum += weight*key
                counters[index].fingerprint.update(key)
            }
        }
    }

    fun query(): IntArray {
        val res = emptyArray<Int>().toMutableSet()
        while (query_one_round(res)) {}
        println(res)
        return res.toIntArray()
    }

    fun query_one_round(set: MutableSet<Int>): Boolean {
        var flag = false
        for (i in 0 until n) {
            val count_i = counters[i].count
            if (count_i.absoluteValue <= threshold) continue
            val sum_i = counters[i].sum
            val fingerprint_i = counters[i].fingerprint
            val item = sum_i.div(count_i)

//            println("item: " + item.roundToInt() + " actual: " + item + " " + fingerprint_i.query(item.roundToInt()))
            // If succeed
            if (abs(item.roundToInt() - item) <= threshold
                    && fingerprint_i.query(item.roundToInt())) {
                // At least one item is recovered
                flag = true
                set.add(item.roundToInt())
                update(item.roundToInt(), count_i*(-1))
            }
        }
        println()
        return flag
    }

    fun merge(summary: SparseRecovery<T>) {
        // merge check
        assert(n == summary.n) { "Unable to apply merge, the number of sizes are not equal $n != ${summary.n}" }
        assert(l == summary.l) { "Unable to apply merge, the number of hash functions are not equal $l != ${summary.l}" }
        assert(maxSize == summary.maxSize) { "Unable to apply merge, the sizes are not equal $maxSize != ${summary.maxSize}" }
        assert(k == summary.k) { "Unable to apply merge, the number of hash functions are not equal $k != ${summary.k}" }

        for (i in 0 until l) {
            for (j in 0 until 10) {
                val item = Random.nextInt().toString().toByteArray()
                assert(hashes[i].digest(item) == summary.hashes[i].digest(item))
                { "Unable to apply merge, the randomness used are different" }
            }
        }

        for (i in 0 until n) {
            counters[i].merge(summary.counters[i])
        }
    }

    private fun getIndex(bytes: ByteArray, max: Int) : Int {
        var result = 0
        var shift = 0
        for (i in 0 until 8) {
            result = result or (bytes[i].toInt() shl shift)
            shift += 8
        }
        return result.absoluteValue.rem(max)
    }
}