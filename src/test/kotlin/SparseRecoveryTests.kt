package frequencies.tests

import org.junit.Test
import sparserecovery.SparseRecovery
import kotlin.random.Random
import kotlin.test.assertFails

class SparseRecoveryTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = SparseRecovery<Int>(100, 5, 20, 3)
        for (i in 0 until 10000) {
            sketch.update(Random.nextInt(), weight)
        }
        sketch.query()
        sketch.query()
    }

    @Test
    fun testMergeFail() {
        val weight = 0.5
        val a = SparseRecovery<Int>(100, 5, 20, 3)
        val b = SparseRecovery<Int>(100, 5)
        for (i in 0 until 10000) {
            val item = Random.nextInt()
            a.update(item, weight)
            b.update(item)
        }
        assertFails("Invalid merge") { a.merge(b) }
        assertFails("Invalid merge") { b.merge(a) }
    }
}