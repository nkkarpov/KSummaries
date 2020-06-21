package frequencies.tests

import frequencies.FrequentItems
import misc.Counter
import kotlin.test.assertEquals
import org.junit.Test

class FrequentItemsTests {
    @Test
    fun testInit() {
        val sketch = FrequentItems<Int>(1)
        sketch.update(1)
        assertEquals(sketch.frequentItems(), listOf(Counter(1, 1.0)))
    }

}