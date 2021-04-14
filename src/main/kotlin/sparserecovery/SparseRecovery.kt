package sparserecovery

import bloom.BloomFilter
import saltedhash.SaltedHash
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class Cell {
    var count = 0.0
    var sum = 0.0
    var fingerprint: BloomFilter<Int>
    val maxSize: Int
    val k: Int

    constructor(maxSize: Int, k: Int) {
        this.maxSize = maxSize
        this.k = k
        fingerprint = BloomFilter<Int>(maxSize, k)
    }

    fun clone(): Cell{
        var copy = Cell(maxSize, k)
        copy.count = this.count
        copy.sum = this.sum
        copy.fingerprint = this.fingerprint.clone()
        return copy
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
    private val counters_records = emptyArray<Cell>().toMutableList()
    private val n: Int
    // l hash functions
    private val hashes: SaltedHash
    private val l: Int
    // parameters for bloom filters
    private val maxSize: Int
    private val k: Int
    // threshold in division
    private val threshold = 0.000001
    private val hashSeed: Int

    constructor(n: Int, l: Int): this(n,l,n,l)

    constructor(n: Int, l: Int, maxSize: Int, k: Int, hashSeed: Int = 100) {
        this.n = if (n > 0) n else error("parameter is negative")
        this.l = if (l > 0) l else error("parameter is negative")
        this.maxSize = if (maxSize > 0) maxSize else error("parameter is negative")
        this.k = if (k > 0) k else error("parameter is negative")
        this.hashSeed = hashSeed

        for (i in 0 until this.n) {
            counters.add(Cell(maxSize, k))
            counters_records.add(Cell(maxSize, k))
        }
        hashes = SaltedHash(5*l, hashSeed)
    }

    fun update(key: Int, weight: Double = 1.0) {
        val set = emptySet<Int>().toMutableSet()
//        println("Insert " + key)
        var count = 0
        var numHash = 0
        // Ensure that each item gets l positions to update
        while (count < l && numHash < 5*l) {
            val index = hashes.getIndex(numHash, key.toString(), n)
            numHash++
            if (set.isEmpty() || (!set.contains(index))) {
                count++
                set.add(index)
                counters[index].count += weight
                counters[index].sum += weight*key
                counters[index].fingerprint.update(key)
            }
        }
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
        for (i in 0 until counters.size) {
            counters_records[i] = counters[i].clone()
        }
        val res = emptyArray<Int>().toMutableSet()
        while (queryOneRound(res)) {}
        if (failCheck()) return null
//        println(res)
        for (i in 0 until counters_records.size) {
            counters[i] = counters_records[i].clone()
        }
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
                if (!set.contains(item)) {
                    update(item, count_i*(-1))
                    set.add(item)
                    flag = true
                }
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