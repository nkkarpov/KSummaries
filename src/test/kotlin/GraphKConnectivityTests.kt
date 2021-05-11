import graphconnnectivity.GraphConnectivity
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