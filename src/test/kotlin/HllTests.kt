package frequencies.tests

import hll.HyperLL
import kotlin.test.assertEquals
import org.junit.Test
import kotlin.test.assertFails
import kotlin.test.assertTrue

class HyperLLTests {
    @Test
    fun testInit() {
        val sketch = HyperLL<Int>(5)
        for (i in 0 until 10) {
            sketch.update(i)
        }
        println(sketch.query())
    }
}