package frequencies.tests

import org.junit.Test
import sampling.ReservoirSampling
import kotlin.math.min
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class ReservoirSamplingTests {
    @Test
    fun testInit() {
        val sketch = ReservoirSampling<Int>(5)
        for (i in 1..10) {
            sketch.update(i)
            assertTrue("Wrong size of sample") { sketch.sample().size == min(i, 5) }
        }
    }

    @Test
    fun testMerge1() {
        val a = ReservoirSampling<Int>(5)
        val b = ReservoirSampling<Int>(5)
        a.update(1)
        b.update(2)
        a.merge(b)
        assertEquals(a.sample().sorted(), listOf(1, 2))
    }

    @Test
    fun testMerge2() {
        val a = ReservoirSampling<Int>(5)
        val b = ReservoirSampling<Int>(5)
        (1..5).forEach { a.update(it) }
        (6..10).forEach { b.update(it) }
        a.merge(b)
        assertTrue(a.sample().all { it in 1..10 })
    }

    @Test
    fun testMerge3() {
        val a = ReservoirSampling<Int>(6)
        val b = ReservoirSampling<Int>(5)
        (10..19).forEach { a.update(it) }
        (20..29).forEach { b.update(it) }
        assertFails("Invalid merge") { a.merge(b) }
    }
}