package frequencies.tests

import filereader.FileReaderBuffer
import org.junit.Test
import sparserecovery.SparseRecovery
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertFails

class SparseRecoveryTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = SparseRecovery<Int>(100, 7, 100, 5)
        for (i in 0 until 20) {
            sketch.update(i.rem(20)+1, weight)
        }
        sketch.query()
    }

    @Test
    fun testMergeFail() {
        val weight = 0.1
        val a = SparseRecovery<Int>(100, 5, 20, 3)
        val b = SparseRecovery<Int>(100, 5)
        val c = SparseRecovery<Int>(100, 5, 20, 3, 20)
        for (i in 0 until 10000) {
            val item = Random.nextInt().absoluteValue.rem(20)
            a.update(item, weight)
            b.update(item)
            c.update(item)
        }
        assertFails("Invalid merge") { a.merge(b) }
        assertFails("Invalid merge") { b.merge(a) }
        assertFails("Invalid merge") { a.merge(c) }
    }

    fun testQueryResults(sr: SparseRecovery<Int>, truth: Set<Int>) {
        val res = sr.query()
        if (res == null) {
            println("Cannot recovery")
        } else if (res.size != truth.size) {
            println("Did not recovery whole set. Recovered ${res.size} items out of ${truth.size}")
        } else {
            for (i in res.indices) {
                if (!truth.contains(res[i])) {
                    println("Recovered unrecognized item")
                    return
                }
            }
            println("Successful recovery")
        }
    }

    fun testFiles(fileName1: String, fileName2: String,
                  n: Int, l: Int, maxSize: Int, k: Int, hashSeed: Int = 100) {
        // Load input files
        val reader1 = FileReaderBuffer(fileName1+".txt")
        val reader2 = FileReaderBuffer(fileName2+".txt")
        // Create data structures
        val sr1 = SparseRecovery<Int>(n, l, maxSize, k, hashSeed)
        val sr2 = SparseRecovery<Int>(n, l, maxSize, k, hashSeed)
        val sr1Copy = SparseRecovery<Int>(n, l, maxSize, k, hashSeed)
        // Create two sets as ground truth
        val t1 = emptySet<Int>().toMutableList()
        val t2 = emptySet<Int>().toMutableList()
        val t3 = emptySet<Int>().toMutableList()

        // Feed input into data structures
        var word: String?
        word = reader1.next()
        while (word != null) {
            sr1.update(word.toInt())
            sr1Copy.update(word.toInt())
            if (!t1.contains(word.toInt())) {
                t1.add(word.toInt())
            }
            if (!t3.contains(word.toInt())) {
                t3.add(word.toInt())
            }
            word = reader1.next()
        }
        word = reader2.next()
        while (word != null) {
            sr2.update(word.toInt())
            if (!t2.contains(word.toInt())) {
                t2.add(word.toInt())
            }
            if (!t3.contains(word.toInt())) {
                t3.add(word.toInt())
            }
            word = reader2.next()
        }

        // Merge
        sr1Copy.merge(sr2)

        testQueryResults(sr1, t1.toSet())
        testQueryResults(sr2, t2.toSet())
        testQueryResults(sr1Copy, t3.toSet())
    }

    @Test
    fun testMergeT1T2() {
        val fileNameT1 = "data/T10I4D100K"
        val fileNameT2 = "data/T40I10D100K"
        testFiles(fileNameT1, fileNameT2, 2000, 5, 500, 5)
    }

    @Test
    fun testMergeT2K() {
        val fileNameT2 = "data/T40I10D100K"
        val fileNameK = "data/kosarak"
        testFiles(fileNameT2, fileNameK, 100000, 5, 1000, 5)
    }
}