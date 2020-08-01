package frequencies.tests

import misc.amssketch.AmsSketch
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFails

class AmsSketchTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = AmsSketch<Int>(20, 1000)
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
        val a = AmsSketch<Int>(d, t)
        val b = AmsSketch<Int>(d, t)
        val weight = 0.5
        for (i in 0 until 10000) {
            val item = Random.nextInt()
            a.update(item, weight)
            b.update(item, weight)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }
}