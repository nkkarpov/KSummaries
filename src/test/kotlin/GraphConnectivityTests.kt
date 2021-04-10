import graphconnnectivity.GraphConnectivity
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertFails

class GraphConnectivityTests {
    @Test
    fun testInit() {
        val sketch = GraphConnectivity(101, 100, 5, 5, 10, 3, 10, 5)
        for (i in 0 until 100) {
            sketch.update(i, i+1)
        }
        sketch.query()
    }

    @Test
    fun testMergeFail() {
        val a = GraphConnectivity(100, 100, 5, 5, 10, 3, 10, 5)
        val b = GraphConnectivity(100, 100, 5, 5, 10, 3, 10, 5, 10)
        for (i in 0 until 10000) {
            val item = Random.nextInt().rem(99).absoluteValue
            a.update(item, item+1)
            b.update(item, item+1)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }

}