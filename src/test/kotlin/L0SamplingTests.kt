package frequencies.tests

import filereader.FileReaderBuffer
import l0sampling.L0Sampling
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertFails

class L0SamplingTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sampler = L0Sampling(20, 100, 5)
        for (i in 0 until 20) {
            sampler.update(i.rem(20)+1, weight)
        }
        sampler.query()
    }

    @Test
    fun testMergeFail() {
        val weight = 0.1
        val a = L0Sampling(20, 100, 10)
        val b = L0Sampling(20, 100)
        for (i in 0 until 10000) {
            val item = Random.nextInt().absoluteValue.rem(20)
            a.update(item, weight)
            b.update(item)
        }
        assertFails("Invalid merge") { a.merge(b) }
        assertFails("Invalid merge") { b.merge(a) }
    }

    fun testFiles(fileName1: String, fileName2: String,
                  m: Int, n: Int, l: Int, maxSize: Int, k: Int, hashSeed: Int = 100) {
        // Load input files
        val reader1 = FileReaderBuffer(fileName1+".txt")
        val reader2 = FileReaderBuffer(fileName2+".txt")
        // Create data structures
        val sp1 = L0Sampling(m, n, l, maxSize, k, hashSeed)
        val sp2 = L0Sampling(m, n, l, maxSize, k, hashSeed)
        val sp1Copy = L0Sampling(m, n, l, maxSize, k, hashSeed)
        // Create two sets as ground truth
        val t1 = emptySet<Int>().toMutableList()
        val t2 = emptySet<Int>().toMutableList()

        // Feed input into data structures
        var word: String?
        word = reader1.next()
        while (word != null) {
            sp1.update(word.toInt())
            sp1Copy.update(word.toInt())
            if (!t1.contains(word.toInt())) {
                t1.add(word.toInt())
            }
            word = reader1.next()
        }
        word = reader2.next()
        while (word != null) {
            sp2.update(word.toInt())
            if (!t2.contains(word.toInt())) {
                t2.add(word.toInt())
            }
            word = reader2.next()
        }

        // Merge
        sp1Copy.merge(sp2)

        val res1 = emptyList<Int>().toMutableList()
        val res2 = emptyList<Int>().toMutableList()
        val res3 = emptyList<Int>().toMutableList()
        for (i in 0 until 10) {
            var item1 = sp1.query()
            var item2 = sp2.query()
            var item3 = sp1Copy.query()
            if (item1 != null) res1.add(item1)
            if (item2 != null) res2.add(item2)
            if (item3 != null) res3.add(item3)
        }
        println(res1)
        println(sp1.tempResults.size)
        println(res2)
        println(sp2.tempResults.size)
        println(res3)
        println(sp1Copy.tempResults.size)
    }

    @Test
    fun testMergeT1T2() {
        val fileNameT1 = "data/T10I4D100K"
        val fileNameT2 = "data/T40I10D100K"
        testFiles(fileNameT1, fileNameT2, 10, 2000, 5, 500, 5)
    }

    @Test
    fun testMergeT2K() {
        val fileNameT2 = "data/T40I10D100K"
        val fileNameK = "data/kosarak"
        testFiles(fileNameT2, fileNameK, 10, 10000, 5, 500, 5)
    }
}