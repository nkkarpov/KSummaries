package tests

import bloom.BloomFilter
import filereader.FileReaderBuffer
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFails

class BloomFilterTests {
    @Test
    fun testInit() {
        val sketch = BloomFilter<Int>(1000, 10)
        for (i in 0 until 100) {
            sketch.update(i)
        }
        for (i in 100 until 200) {
            sketch.query(i)
        }
    }

    @Test
    fun testMergeFail() {
        val a = BloomFilter<Int>(100, 10)
        val b = BloomFilter<Int>(50, 10)
        for (i in 0 until 10000) {
            val item = Random.nextInt()
            a.update(item)
            b.update(item)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }

    fun reportErrorRate(bf: BloomFilter<String>, dataType: String): Double {
        if (dataType == "Int") {
            var error_rate = 0.0
            var errors = 0
            for (i in 100000 until 200000) {
                val item = i.toString()
                if (bf.query(item))
                    errors += 1
            }
            error_rate = errors / 100000.0
            return error_rate

        } else if (dataType == "String") {
            return 0.0
        } else {
            error("unrecognized data type")
        }
    }

    fun testFiles(fileName1: String, fileName2: String, maxSize: Int, k: Int) {
        val reader1 = FileReaderBuffer(fileName1)
        val reader2 = FileReaderBuffer(fileName2)
        val bf1 = BloomFilter<String>(maxSize, k)
        val bf2 = BloomFilter<String>(maxSize, k)

        // bf1 read file1; bf2 read file2
        var word: String?
        word = reader1.next()
        while (word != null) {
            bf1.update(word)
            word = reader1.next()
        }
        word = reader2.next()
        while (word != null) {
            bf2.update(word)
            word = reader2.next()
        }

        // test error rate on bf1
        val errorRate1 = reportErrorRate(bf1, "Int")
        println(("Error rate on " + fileName1 + " dataset with %d slots and %d hash functions " +
                "is %.2f %%").format(maxSize, k, errorRate1*100))

        // test error rate on bf2
        val errorRate2 = reportErrorRate(bf2, "Int")
        println(("Error rate on " + fileName2 + " dataset with %d slots and %d hash functions " +
                "is %.2f %%").format(maxSize, k, errorRate2*100))

        // merge bf2 to bf1
        bf1.merge(bf2)
        val errorRateMerged = reportErrorRate(bf1, "Int")

        println(("Error rate on" + fileName1 + " and " + fileName2
                + "merged dataset with %d slots and %d hash functions " +
                "is %.2f %%").format(maxSize, k, errorRateMerged*100))

    }

    @Test
    fun testMergeT1T2() {
        val fileNameT1 = "data/T10I4D100K.txt"
        val fileNameT2 = "data/T40I10D100K.txt"
        testFiles(fileNameT1, fileNameT2, 10000, 5)
    }

    @Test
    fun testMergeT2K() {
        val fileNameT2 = "data/T40I10D100K.txt"
        val fileNameK = "data/kosarak.txt"
        testFiles(fileNameT2, fileNameK, 500000, 7)
    }

    @Test
    fun testMergeN1N2() {
        val fileNameN1 = "data/n1.txt"
        val fileNameN2 = "data/n2.txt"
        testFiles(fileNameN1, fileNameN2, 50000000, 7)
    }
}