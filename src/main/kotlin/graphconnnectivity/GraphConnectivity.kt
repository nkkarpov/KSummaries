/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

This algorithm detects if the a graph is k-connected.
 */

package graphconnnectivity

import l0sampling.L0Sampling

class GraphConnectivity {
    // n: #nodes
    val n: Int
    // l0 samplers to store edge information
    var samplers: Array<L0Sampling>
    // number of connected components
    var cc: Int
    // connectivity array
    private var connectivityArray: Array<Int>

    // parameters for sampler: levels, size_recovery, num_hash_recovery
    // parameters for bloomFilter: maxSize_fingerprint, num_hash_fingerprint
    constructor(n: Int,
                levels: Int, size_recovery: Int, num_hash_recovery: Int,
                maxSize_fingerprint: Int, num_hash_fingerprint: Int,
                hashSeed: Int = 100) {
        this.n = n
        this.cc = n
        connectivityArray = Array(n, {0})
        for (i in 0 until n) {
            connectivityArray[i] = i
        }
        samplers = Array(n) { L0Sampling(levels, size_recovery, num_hash_recovery,
            maxSize_fingerprint, num_hash_fingerprint, hashSeed) }
    }

    // Give id of two nodes
    fun update(n1: Int, n2: Int, weight:Double = 1.0) {
        if (n1 == n2) return
        // Make sure that n1 < n2, parameter weight is mandatory in case of deletion
        if (n1 > n2) return update(n2, n1, weight)

        val index = edgeToInt(n1, n2)
        samplers[n1].update(index, weight)
        samplers[n2].update(index, -1*weight)
    }

    fun query(): Int {
        var flag = true
        // while there is still change in connected components
        while (flag) {
            flag = false
            // check for each a supernode
            for (i in 0 until n) {
                val decreased = decreaseCC(i)

                // if flag is false, update flag
                if (!flag) flag = decreased.first
            }
        }
//        println(connectivityArray.toList())
        return cc
    }

    fun merge(summary: GraphConnectivity) {
        assert(n == summary.n) { "Unable to apply merge, the size of graph nodes is not equal $n != ${summary.n}" }

        for (i in 0 until n) {
            samplers[i].merge(summary.samplers[i])
        }
    }

    fun decreaseCC(i: Int): Pair<Boolean, Int?> {
        if (i == find(i)) {
            // output a edge
            var index = samplers[i].query()
            // the sampler returns an edge
            if (index != null) {
                // peal edge from index
                val edgeArray = intToEdge(index)
                val n1 = edgeArray[0]
                var n2 = edgeArray[1]

                // the returned value is not correct
                if (n1<0 || n1 >=n || n2 < 0 || n2>=n) return Pair(false, index)

                // make sure i == n1
                if (i == n2) {
                    n2 = n1
                }
                // self loop
                if (i == n2) return Pair(false, index)


                // Connected component decreases
                if (!same_componnet(i, n2)) {
                    // find n2's supernode
                    val n_super = find(n2)
                    // merge to a supernode
                    union(i, n2)
                    // set flag to be ture, continue to connect
                    if (i < n_super) {
                        samplers[i].merge(samplers[n_super])

                    }
                    else {
                        samplers[n_super].merge(samplers[i])
                    }
                    return Pair(true, index)
                }
                // the edge does not connect components
                return Pair(false, index)
            }
            // sampler returns null
            return Pair(false, index)
        }
        // i is not supernode
        return Pair(false, null)
    }

    // path compression
    private fun find(n: Int): Int {
        var n_ = n
        if (connectivityArray[n_] != n_) {
            connectivityArray[n_] = find(connectivityArray[n_])
        }
        return connectivityArray[n_]
    }

    private fun same_componnet(n1: Int, n2: Int): Boolean {
        return (find(n1) == find(n2))
    }

    private fun union(n1: Int, n2: Int) {
        if (same_componnet(n1, n2)) {
            println("same component, no need to union " + n1 +" and "+ n2)
            return
        }
        cc -= 1
        if (find(n1) < find(n2)) connectivityArray[find(n2)] = find(n1)
        else connectivityArray[find(n1)] = find(n2)
    }

    private fun edgeToInt (n1: Int, n2: Int): Int {
        return n1*n + n2
    }
    private fun intToEdge (index: Int): Array<Int> {
        val n1 = index / n
        val n2 = index % n
        return arrayOf(n1, n2)
    }
}