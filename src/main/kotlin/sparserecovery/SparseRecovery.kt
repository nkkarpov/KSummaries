package sparserecovery

import bloom.BloomFilter
import saltedhash.SaltedHash
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

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
    private var hashes: SaltedHash
    private var l: Int
    // parameters for bloom filters
    private var maxSize: Int
    private var k: Int
    // threshold in division
    private val threshold = 0.000001

    constructor(n: Int, l: Int): this(n,l,n,l)

    constructor(n: Int, l: Int, maxSize: Int, k: Int, hashSeed: Int = 100) {
        this.n = if (n > 0) n else error("parameter is negative")
        this.l = if (l > 0) l else error("parameter is negative")
        this.maxSize = if (maxSize > 0) maxSize else error("parameter is negative")
        this.k = if (k > 0) k else error("parameter is negative")

        for (i in 0 until this.n) {
            counters.add(Cell(maxSize, k))
        }
        hashes = SaltedHash(l, hashSeed)
    }

    fun update(key: Int, weight: Double = 1.0) {
        val set = emptySet<Int>().toMutableSet()
//        println("Insert " + key)
        for (i in 0 until l) {
            val index = hashes.getIndex(i, key.toString(), n)
            if (set.isEmpty() || (!set.contains(index))) {
                set.add(index)
//                println("item " + key + " in cell " + index)
                counters[index].count += weight
                counters[index].sum += weight*key
                counters[index].fingerprint.update(key)
            }
        }
    }

    fun query(): List<Int>? {
        val res = emptyArray<Int>().toMutableSet()
        while (queryOneRound(res)) {}
        if (failCheck()) return null
//        println(res)
        return res.toList()
    }

    // Return true if the recovery fails
    private fun failCheck(): Boolean{
        for (i in 0 until n) {
            if (counters[i].count > threshold) return true
        }
        return false
    }

    private fun queryOneRound(set: MutableSet<Int>): Boolean {
        var flag = false
        for (i in 0 until n) {
            val count_i = counters[i].count
            // No item found in the cell
            if (count_i.absoluteValue <= threshold) continue

            val sum_i = counters[i].sum
            val fingerprint_i = counters[i].fingerprint
            val possibleItem = sum_i.div(count_i)
            // Not close to an integer
            if ((possibleItem - possibleItem.roundToInt()).absoluteValue > threshold) continue

            val item = possibleItem.roundToInt()
            // If item fits the fingerprint
            if (fingerprint_i.query(item)) {
                // At least one item is recovered
                flag = true
                set.add(item)
                update(item, count_i*(-1))
            }
        }
        return flag
    }

    fun merge(summary: SparseRecovery<T>) {
        // merge check
        assert(n == summary.n) { "Unable to apply merge, the number of sizes are not equal $n != ${summary.n}" }
        assert(l == summary.l) { "Unable to apply merge, the number of hash functions are not equal $l != ${summary.l}" }
        assert(maxSize == summary.maxSize) { "Unable to apply merge, the sizes are not equal $maxSize != ${summary.maxSize}" }
        assert(k == summary.k) { "Unable to apply merge, the number of hash functions are not equal $k != ${summary.k}" }
        assert(hashes.mergeableWith(summary.hashes)) { "Unable to apply merge, the number of hash functions are not equal $k != ${summary.k}" }

        for (i in 0 until n) {
            counters[i].merge(summary.counters[i])
        }
    }

}