package frequencies.tests

import bloom.BloomFilter
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
}