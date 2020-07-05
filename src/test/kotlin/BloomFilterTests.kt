package frequencies.tests

import bloom.BloomFilter
import org.junit.Test

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
}