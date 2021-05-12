import graphkconnnectivity.GraphKConnectivity
import l0sampling.L0Sampling
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertFails

class GraphKConnectivityTests {
    @Test
    fun testInit() {
        val sketch = GraphKConnectivity(10, 1, 5, 10, 3, 10, 5)
        for (i in 0 until 9) {
            sketch.update(i, i+1)
        }
        sketch.update(9, 0)
        println(sketch.query())
    }

    @Test
    fun testK() {
        val n = 100
        val k = 4
        val sketch = GraphKConnectivity(n, k, 5, 200, 5, 100, 5)
        for (i in 0 until 100) {
            val set = mutableSetOf<Int>()
            while (set.size < k+2) {
                val node = Random.nextInt().rem(n).absoluteValue
                if (node != i) set.add(node)
            }
            for (j in set) {
                sketch.update(i, j)
            }
        }
        println(sketch.query())
    }


    @Test
    fun testMergeFail() {
        val a = GraphKConnectivity(100,  5, 5, 10, 3, 10, 5)
        val b = GraphKConnectivity(100,  5, 5, 10, 3, 10, 5, 10)
        for (i in 0 until 10000) {
            val item = Random.nextInt().rem(99).absoluteValue
            a.update(item, item+1)
            b.update(item, item+1)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }

}