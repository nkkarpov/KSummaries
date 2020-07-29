package frequencies.tests

import conservativesketch.ConservativeSketch
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFails

class ConservativeSketchTests {
    @Test
    fun testInit() {
        val weight = 0.5
        val sketch = ConservativeSketch<Int>(5, 50)
        for (i in 0 until 1000) {
            sketch.update(Random.nextInt(), weight)
        }
        sketch.query(Random.nextInt())
        sketch.query(Random.nextInt())
    }

    @Test
    fun testMergeFail() {
        val d = 10
        val t = 100
        val a = ConservativeSketch<Int>(d, t)
        val b = ConservativeSketch<Int>(d, t)
        val weight = 0.5
        for (i in 0 until 10000) {
            val item = Random.nextInt()
            a.update(item, weight)
            b.update(item, weight)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }
}