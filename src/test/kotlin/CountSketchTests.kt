package frequencies.tests

import countsketch.CountSketch
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFails

class CountSketchTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = CountSketch<Int>(20, 1000)
        for (i in 0 until 10000) {
            sketch.update(Random.nextInt(), weight)
        }
        sketch.query(Random.nextInt())
        sketch.query(Random.nextInt())
    }

    @Test
    fun testMergeFail() {
        val d = 20
        val t = 1000
        val a = CountSketch<Int>(d, t)
        val b = CountSketch<Int>(d, t)
        val weight = 0.5
        for (i in 0 until 10000) {
            val item = Random.nextInt()
            a.update(item, weight)
            b.update(item, weight)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }
}