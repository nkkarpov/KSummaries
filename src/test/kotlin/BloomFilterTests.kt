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

    fun testFile(fileName: String, maxSize: Int, k: Int) {
        val reader = FileReaderBuffer(fileName)
        val bf = BloomFilter<String>(maxSize, k)
        var word: String?
        word = reader.next()
        while (word != null) {
            bf.update(word)
            word = reader.next()
        }

        var error_rate = 0.0
        var errors = 0
        for (i in 1001 until 2000) {
            val item = i.toString()
            if (bf.query(item))
                errors += 1
        }
        error_rate = errors / 1000.0
        println(("Error rate on T40I10D100K dataset with %d slots and %d hash functions " +
                "is %.2f %%").format(maxSize, k, error_rate*100))
    }

    @Test
    fun testT40I10D100K() {
        val fileName = "data/T40I10D100K.txt"
        val maxSize = 100
        val k = 5
        testFile(fileName, maxSize, k)
    }
}