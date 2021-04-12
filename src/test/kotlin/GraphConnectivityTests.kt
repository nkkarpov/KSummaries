import graphconnnectivity.GraphConnectivity
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertFails

class GraphConnectivityTests {
    @Test
    fun testInit() {
        val sketch = GraphConnectivity(10, 100, 5, 5, 10, 3, 10, 5)
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