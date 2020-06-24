package frequencies.tests

import frequencies.FrequentItems
import misc.Counter
import kotlin.test.assertEquals
import org.junit.Test
import kotlin.test.assertFails
import kotlin.test.assertTrue

class FrequentItemsTests {
    @Test
    fun testInit() {
        val sketch = FrequentItems<Int>(1)
        sketch.update(1)
        assertEquals(sketch.frequentItems(), listOf(Counter(1, 1.0)))
        sketch.update(1, 5.0)
        assertEquals(sketch.frequentItems(), listOf(Counter(1, 6.0)))
    }

    @Test
    fun testEstimate() {
        val k = 2
        val sketch = FrequentItems<Int>(k)
        sketch.update(1, 2.0)
        sketch.update(2, 3.0)
        sketch.update(3, 1.0)
        assertTrue(sketch.estimate(1) >= 2 - 6.0 / k, "Bad estimator of 1")
        assertTrue(sketch.estimate(1) >= 3 - 6.0 / k, "Bad estimator of 2")
        assertTrue(sketch.estimate(1) >= 1 - 6.0 / k, "Bad estimator of 3")
    }

    @Test
    fun testMerge() {
        val k = 2
        val a = FrequentItems<Int>(k)
        val b = FrequentItems<Int>(k)
        a.update(1, 3.0)
        b.update(7, 4.0)
        a.update(2, 2.0)
        b.update(3, 2.0)
        a.update(4, 1.0)
        b.update(5, 1.0)
        a.merge(b)
        assertEquals(a.frequentItems().map { it.key }.sorted(), listOf(3, 7))
    }

    @Test
    fun testMergeFail() {
        val a = FrequentItems<Int>(1)
        val b = FrequentItems<Int>(2)
        a.update(1)
        b.update(2)
        b.update(2)
        a.update(2)
        assertFails("Invalid merge") { a.merge(b) }

    }

}