package frequencies.tests

import fingerprints.FingerPrints
import org.junit.Test
import kotlin.random.Random.Default.nextInt
import kotlin.test.assertFails

class FingerPrintsTests {
    @Test
    fun testInit() {
        val sketchA = FingerPrints<Int>()
        val sketchB = FingerPrints<Int>()
        val sketchC = FingerPrints<Int>()
        val weight = 1.0

        for (i in 0 until 1000) {
            var j = nextInt()
            sketchA.update(j, weight)
            sketchB.update(j, weight)
            j = nextInt()
            sketchC.update(j, weight)
        }
        println(sketchA.query(sketchB))
        println(sketchA.query(sketchC))
        sketchA.merge(sketchC)
        println(sketchA.query(sketchC))
    }

    @Test
    fun testMerge() {
        val a = FingerPrints<Int>()
        val b = FingerPrints<Int>()
        val weight = 1.0
        for (i in 0 until 10000) {
            val item = nextInt()
            a.update(item, weight)
            b.update(item, weight)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }
}