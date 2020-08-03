package frequencies.tests

import org.junit.Test
import sparserecovery.SparseRecovery
import kotlin.random.Random

class SparseRecoveryTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = SparseRecovery<Int>(100)
        for (i in 0 until 10000) {
            sketch.update(Random.nextInt(), weight)
        }
        sketch.query()
        sketch.query()
    }
}