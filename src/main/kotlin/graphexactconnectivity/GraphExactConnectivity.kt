/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

This algorithm returns whether an input graph is connected.
Current implementation does not support merge yet.
 */

package graphexactconnectivity

import graphconnnectivity.GraphConnectivity
import l0sampling.L0Sampling
import kotlin.math.absoluteValue

class GraphExactConnectivity {
    // n: #nodes
    val n: Int
    private var array: Array<Int>
    private var count = 0

    constructor(n: Int) {
        this.n = n
        array = Array(n, {0})
        for (i in 0 until n) {
            array[i] = i
        }
    }

    // Give id of two nodes
    fun update(n1: Int, n2: Int) {
        // Self loop. Should not exist
        if (n1 == n2) return
        // Make sure that n1 < n2
        if (n1 > n2) return update(n2, n1)

        // Already in the same component
        if (same_componnet(n1, n2)) return

        // Count this edge
        count += 1
        array[n2] = n1
    }

    fun query() : Boolean {
        return count == n-1
    }

    private fun same_componnet(n1: Int, n2: Int): Boolean {
        var n1_ = n1
        var n2_ = n2
        while (array[n1_] != n1_) {
            n1_ = array[n1_]
        }
        while (array[n2_] != n2_) {
            n2_ = array[n2_]
        }
        return n1_ == n2_
    }

//    fun merge(summary: GraphExactConnectivity) {
//        assert(n == summary.n) { "Unable to apply merge, the size of graph is not equal $n != ${summary.n}" }
//    }
}
