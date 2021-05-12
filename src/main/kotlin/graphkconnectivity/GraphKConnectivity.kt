/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

This algorithm returns the k connectivity of an input graph.
 */

package graphkconnnectivity

import graphconnnectivity.GraphConnectivity

class GraphKConnectivity {
    // n: #nodes
    val n: Int
    val k: Int
    val hashSeed: Int
    // k GraphConnectivity to store sketches
    private var connectivities: Array<GraphConnectivity>
    // array to record the connectivity on some level
    private var connectivityArray: Array<Int>

    // connectivity parameter: k
    // parameters for sampler: levels, size_recovery, num_hash_recovery
    // parameters for bloomFilter: maxSize_fingerprint, num_hash_fingerprint
    constructor(n: Int, k:Int,
                levels: Int, size_recovery: Int, num_hash_recovery: Int,
                maxSize_fingerprint: Int, num_hash_fingerprint: Int,
                hashSeed: Int = 100) {
        this.n = n
        this.k = k
        this.hashSeed = hashSeed
        // Each node is its own supernode
        connectivityArray = Array(n, {0})
        for (i in 0 until n) {
            connectivityArray[i] = i
        }
        connectivities = Array(k) { GraphConnectivity(n, levels, size_recovery, num_hash_recovery,
            maxSize_fingerprint, num_hash_fingerprint,
            hashSeed)}
    }

    // Give id of two nodes
    fun update(n1: Int, n2: Int, weight: Double = 1.0) {
        if (n1 == n2) return
        // Make sure that n1 < n2
        if (n1 > n2) return update(n2, n1, weight)

        for (i in 0 until k) {
            connectivities[i].update(n1, n2, weight)
        }
    }

    fun query(): Boolean {
        for (i in 0 until k-1) {
            var flag = true
            while(flag) {
                flag = false
                for (j in 0 until n) {
                    val decreased = connectivities[i].decreaseCC(j)

                    // update flag is flag is false
                    if (!flag) flag = decreased.first

                    // If the sampled edge indeed decreases cc
                    if (decreased.first) {
                        val edgeIndex = decreased.second
                        if (edgeIndex != null) {
                            val nodes = intToEdge(edgeIndex)
                            // Delete that edge from following sketches
                            for (j in i+1 until k) {
                                connectivities[j].update(nodes.first, nodes.second, -1.0)
                            }
                        }
                    }
                }
            }
            flag = true
            while(flag) {
                flag = false
                for (j in 0 until n) {
                    val decreased = connectivities[i].decreaseCC(j)

                    // update flag is flag is false
                    if (!flag) flag = decreased.first

                    // If the sampled edge indeed decreases cc
                    if (decreased.first) {
                        val edgeIndex = decreased.second
                        if (edgeIndex != null) {
                            val nodes = intToEdge(edgeIndex)
                            // Delete that edge from following sketches
                            for (j in i+1 until k) {
                                connectivities[j].update(nodes.first, nodes.second, -1.0)
                            }
                        }
                    }
                }
            }
            // Not connected at level i
            var cc_current_level = connectivities[i].query()
            cc_current_level = connectivities[i].query()
            cc_current_level = connectivities[i].query()
            if (cc_current_level != 1) {
                val level = i+1
//                println("Connected component " + cc_current_level + " at level " + level)
                return false
            }
        }
        val cc_final_level = connectivities[k-1].query()
//        println("Connected component " + cc_final_level + " at final level " + k)
        return cc_final_level == 1
    }

    fun merge(summary: GraphKConnectivity) {
        assert(n == summary.n) { "Unable to apply merge, the size of graph nodes is not equal $n != ${summary.n}" }
        assert(k == summary.k) { "Unable to apply merge, the connectivity constant is not equal $k != ${summary.k}" }
        assert(hashSeed == summary.hashSeed) { "Unable to apply merge, the hash randomness not equal!" }

        for (i in 0 until k) {
            connectivities[i].merge(summary.connectivities[i])
        }
    }

    private fun intToEdge (index: Int): Pair<Int, Int> {
        val n1 = index / n
        val n2 = index % n
        return Pair(n1, n2)
    }
}
