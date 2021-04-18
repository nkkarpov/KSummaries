import graphconnnectivity.GraphConnectivity
import l0sampling.L0Sampling
import org.junit.Test
import sparserecovery.SparseRecovery
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertFails

class GraphConnectivityTests {
    @Test
    fun testInit() {
        val sketch = GraphConnectivity(10,  5, 5, 10, 3, 10, 5)
        for (i in 0 until 9) {
            sketch.update(i, i+1)
        }
        sketch.update(9, 0)
//        for (i in 0 until 99) {
//            sketch.update(i, i+2)
//        }
//        sketch.update(100, 1)
        println(sketch.query())
    }

    @Test
    fun testMergeFail() {
        val a = GraphConnectivity(100,  5, 5, 10, 3, 10, 5)
        val b = GraphConnectivity(100,  5, 5, 10, 3, 10, 5, 10)
        for (i in 0 until 10000) {
            val item = Random.nextInt().rem(99).absoluteValue
            a.update(item, item+1)
            b.update(item, item+1)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }

    fun testRecoveryMerge() {
        val a = L0Sampling(5, 100, 3)
        val b = L0Sampling(5, 100, 3)
        val c = L0Sampling(5, 100, 3)

        for (i in 0 until 10) {
            a.update(i, 1.0)
        }
        b.update(9, -1.0)
        b.update(1, -1.0)
        a.merge(b)
        println(a.query())
        c.update(2, -1.0)
        c.update(3, -1.0)
        a.merge(c)
        println(a.query())
    }

}