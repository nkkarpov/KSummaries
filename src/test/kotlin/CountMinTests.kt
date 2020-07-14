package frequencies.tests

import countmin.CountMin
import org.junit.Test
import kotlin.random.Random.Default.nextInt

class CountMinTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = CountMin<Int>(10, 100)
        for (i in 0 until 10000) {
            sketch.update(nextInt(), weight)
        }
        sketch.query(nextInt())
    }
}