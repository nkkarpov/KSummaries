package frequencies.tests

import hll.HyperLL
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFails

class HyperLLTests {
    @Test
    fun testInit() {
        val sketch = HyperLL<Int>(5)
        for (i in 0 until 10) {
            sketch.update(i)
        }
        println(sketch.query())
    }

    @Test
    fun testMergeFail() {
        val a = HyperLL<Int>(20)
        val b = HyperLL<Int>(30)
        for (i in 0 until 10000) {
            val item = Random.nextInt()
            a.update(item)
            b.update(item)
        }
        a.query()
        b.query()
        assertFails("Invalid merge") { a.merge(b) }
    }
}