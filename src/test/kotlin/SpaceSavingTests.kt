package frequencies.tests

import org.junit.Test
import spacesaving.SpaceSaving

class SpaceSavingTests {
    @Test
    fun testInit() {
        val a = SpaceSaving<Int>(10)
        val b = SpaceSaving<Int>(10)
        for (i in 0 until 1000) {
            a.update(i)
            b.update(i)
        }
        a.merge(b)
    }
}