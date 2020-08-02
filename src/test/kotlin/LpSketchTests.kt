package frequencies.tests

import lpsketch.LpSketch
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFails

class LpSketchTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = LpSketch<Int>(2, 20)
        for (i in 0 until 10000) {
            sketch.update(Random.nextInt(), weight)
        }
        sketch.query()
        sketch.query()
    }

    @Test
    fun testMergeFail() {
        val p = 2
        val k = 100
        val a = LpSketch<Double>(p, k)
        val b = LpSketch<Double>(p, k)
        val weight = 1.0
        for (i in 0 until 10000) {
            val item = Random.nextDouble()
            a.update(item, weight)
            b.update(item, weight)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }
}