package frequencies.tests

import hll.HyperLL
import org.junit.Test
import spacesaving.SpaceSaving
import kotlin.random.Random
import kotlin.test.assertFails

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

    @Test
    fun testQuery() {
        val a = SpaceSaving<Int>(10)
        for (i in 0 until 1000) {
            val item = Random.nextInt()
            a.update(item)
        }
        for (i in 0 until 10) {
            val item = Random.nextInt()
            a.query(item)
        }
    }

    @Test
    fun testMergeFail() {
        val a = SpaceSaving<Int>(20)
        val b = SpaceSaving<Int>(30)
        for (i in 0 until 10000) {
            val item = Random.nextInt()
            a.update(item)
            b.update(item)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }
}