package frequencies.tests

import l0sampling.L0Sampling
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertFails

class L0SamplingTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sampler = L0Sampling(20, 100, 5)
        for (i in 0 until 20) {
            sampler.update(i.rem(20)+1, weight)
        }
        sampler.query()
    }

    @Test
    fun testMergeFail() {
        val weight = 0.1
        val a = L0Sampling(20, 100)
        val b = L0Sampling(20, 100)
        for (i in 0 until 10000) {
            val item = Random.nextInt().absoluteValue.rem(20)
            a.update(item, weight)
            b.update(item)
        }
        assertFails("Invalid merge") { a.merge(b) }
        assertFails("Invalid merge") { b.merge(a) }
    }
}