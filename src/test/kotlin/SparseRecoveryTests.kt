package frequencies.tests

import org.junit.Test
import sparserecovery.SparseRecovery
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertFails

class SparseRecoveryTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = SparseRecovery<Int>(100, 7, 100, 5)
        for (i in 0 until 20) {
            sketch.update(i.rem(20)+1, weight)
        }
        sketch.query()
    }

    @Test
    fun testMergeFail() {
        val weight = 0.1
        val a = SparseRecovery<Int>(100, 5, 20, 3)
        val b = SparseRecovery<Int>(100, 5)
        val c = SparseRecovery<Int>(100, 5, 20, 3)
        for (i in 0 until 10000) {
            val item = Random.nextInt().absoluteValue.rem(20)
            a.update(item, weight)
            b.update(item)
            c.update(item)
        }
        assertFails("Invalid merge") { a.merge(b) }
        assertFails("Invalid merge") { b.merge(a) }
        assertFails("Invalid merge") { a.merge(c) }
    }
}