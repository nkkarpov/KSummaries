/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

This algorithm returns the connectivity of an input graph.
 */

package graphconnnectivity

import l0sampling.L0Sampling

class GraphConnectivity {
    // n: #nodes, m: #edges
    val n: Int
    val m: Int
    // l0 samplers to store edge information
    var samplers: Array<L0Sampling>
    // number of connected components
    var cc: Int
    // connectivity array
    private var connectivityArray: Array<Int>

    // n by r samplers
    // parameters for sampler: levles, size_recovery, num_hash_recovery
    // parameters for bloomFilter: maxSize_fingerprint, num_hash_fingerprint
    constructor(n: Int, m: Int, r: Int,
                levels: Int, size_recovery: Int, num_hash_recovery: Int,
                maxSize_fingerprint: Int, num_hash_fingerprint: Int,
                hashSeed: Int = 100) {
        this.n = n
        this.m = m
        this.cc = n
        connectivityArray = Array(n, {0})
        for (i in 0 until n) {
            connectivityArray[i] = i
        }
        samplers = Array(n) { L0Sampling(levels, size_recovery, num_hash_recovery,
                               maxSize_fingerprint, num_hash_fingerprint, hashSeed) }
    }

    // Give id of two nodes
    fun update(n1: Int, n2: Int) {
        if (n1 == n2) return
        // Make sure that n1 < n2
        if (n1 > n2) return update(n2, n1)

        samplers[n1].update(n2, 1.0)
        samplers[n2].update(n1, -1.0)
    }

    fun query(): Int {
        var flag = true
        // while there is still change in connected components
        while (flag) {
            flag = false
            for (i in 0 until n) {
                // check for each a supernode
                if (i == connectivityArray[i]) {
                    // output a edge
                    val n2 = samplers[i].query()
                    // if connect to another component
                    if (n2 != null && !same_componnet(i, n2)) {
                        // decrease count of connected component
                        cc -= 1
                        // merge to a supernode
                        union(i, n2)
                        // find n2's supernode
                        val n_super = connectivityArray[n2]
                        // set flag to be ture
                        flag = true
                        if (i < n_super) {
                            samplers[i].merge(samplers[n_super])
                        } else {
                            samplers[n_super].merge(samplers[i])
                        }
                    }
                }
            }
        }
        return cc
    }

    fun merge(summary: GraphConnectivity) {
        assert(n == summary.n) { "Unable to apply merge, the size of rows is not equal $n != ${summary.n}" }

        for (i in 0 until n) {
            samplers[i].merge(summary.samplers[i])
        }
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
        if (same_componnet(n1, n2)) return
        if (find(n1) < find(n2)) connectivityArray[n2] = find(n1)
        else connectivityArray[n1] = connectivityArray[n2]
    }
}