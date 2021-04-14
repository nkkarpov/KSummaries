/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

This algorithm returns whether an input graph is bipartite.
Current implementation does not support merge yet.
 */

package graphexactbipartiteness

class GraphExactBipartiteness {
    // n: #nodes
    val n: Int
    private var graph: Array<MutableList<Int>>
    private var array: Array<Int>
    private var bipartite = true

    constructor(n: Int) {
        this.n = n
        graph = Array(n){emptyList<Int>().toMutableList()}
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

        // if still bipartite
        if (bipartite) {
            graph[n1].add(n2)
            bipartite = containsCycle()
        }
    }

    fun query(): Boolean {
        return bipartite
    }

    // check cycle by DFS
    @OptIn(ExperimentalStdlibApi::class)
    private fun containsCycle(): Boolean {
        var visited = Array<Boolean>(n){false}
        // find a node to start
        var begin = -1
        for (i in 0 until n) {
            if (graph[i].size > 0) {
                begin = i
                break
            }
        }
        // No edge added yet
        if (begin == -1) return true

        var stack = ArrayDeque<Int>()
        stack.addFirst(begin)
        while (stack.isNotEmpty()) {
            var b = stack.removeFirst()
            // if b is visited, contains a cycle
            if (visited[b]) return false

            visited[b] = true
            for (ele in graph[b]) {
                stack.addFirst(ele)
            }
        }
        return false
    }
}