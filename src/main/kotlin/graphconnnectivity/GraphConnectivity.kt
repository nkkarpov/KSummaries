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
    // number of l0 sampler for each node (log n)
    val r: Int
    // l0 samplers to store edge information
    var samplers: Array<Array<L0Sampling>>

    // n by r samplers
    // parameters for sampler: levles, size_recovery, num_hash_recovery
    // parameters for bloomFilter: maxSize_fingerprint, num_hash_fingerprint
    constructor(n: Int, m: Int, r: Int,
                levels: Int, size_recovery: Int, num_hash_recovery: Int,
                maxSize_fingerprint: Int, num_hash_fingerprint: Int,
                hashSeed: Int = 100) {
        this.n = n
        this.m = m
        this.r = r
        samplers = Array(n) { Array(r) { L0Sampling(levels, size_recovery,num_hash_recovery,
                                                    maxSize_fingerprint, num_hash_fingerprint, hashSeed) } }
    }

    // Give id of two nodes
    fun update(n1: Int, n2: Int) {
        if (n1 == n2) return
        // Make sure that n1 < n2
        if (n1 > n2) return update(n2, n1)

        for (j in 0 until r) {
            samplers[n1][j].update(n1*n+n2, 1.0)
            samplers[n2][j].update(n1*n+n2, -1.0)
        }
    }

    fun query() {}

    fun merge(summary: GraphConnectivity) {
        assert(n == summary.n) { "Unable to apply merge, the size of rows is not equal $n != ${summary.n}" }
        assert(r == summary.r) { "Unable to apply merge, the size of rows is not equal $r != ${summary.r}" }

        for (i in 0 until n) {
            for (j in 0 until r) {
                samplers[i][j].merge(summary.samplers[i][j])
            }
        }
    }
}