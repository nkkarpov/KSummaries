package frequencies.tests

import countmin.CountMin
import org.junit.Test
import kotlin.random.Random.Default.nextInt
import kotlin.test.assertFails

class CountMinTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = CountMin<Int>(10, 100)
        for (i in 0 until 10000) {
            sketch.update(nextInt(), weight)
        }
        sketch.query(nextInt())
        sketch.query(nextInt())
    }

    @Test
    fun testMergeFail() {
        val d = 10
        val t = 100
        val a = CountMin<Int>(d, t)
        val b = CountMin<Int>(d, t)
        val weight = 0.1
        for (i in 0 until 10000) {
            val item = nextInt()
            a.update(nextInt(), weight)
            b.update(nextInt(), weight)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }
}