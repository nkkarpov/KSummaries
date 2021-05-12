import graphkconnnectivity.GraphKConnectivity
import graphmincut.GraphMinCut
import kotlin.random.Random
import kotlin.test.assertFails
import org.junit.Test
import kotlin.math.absoluteValue

class GraphMinCutTests {
    @Test
    fun testInit() {
        val sketch = GraphMinCut(10, 3, 1, 5, 10, 3, 10, 5)
        for (i in 0 until 9) {
            sketch.update(i, i+1)
        }
        sketch.update(9, 0)
        println(sketch.query())
    }

    @Test
    fun testK() {
        val n = 100
        val levels = 5
        val k = 2
        val connectivity = 10
        val sketch = GraphMinCut(n, levels, k, 5, 200, 5, 100, 5)
        for (i in 0 until 100) {
            val set = mutableSetOf<Int>()
            while (set.size < connectivity) {
                val node = Random.nextInt().rem(n).absoluteValue
                if (node != i && node >= 0 && node < n) set.add(node)
            }
            for (j in set) {
                sketch.update(i, j)
            }
        }
        println(sketch.query())
    }


    @Test
    fun testMergeFail() {
        val a = GraphMinCut(100,  1, 5, 5, 10, 3, 10, 5)
        val b = GraphMinCut(100,  1, 5, 5, 10, 3, 10, 5, 10)
        for (i in 0 until 10000) {
            val item = Random.nextInt().rem(99).absoluteValue
            a.update(item, item+1)
            b.update(item, item+1)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }

}