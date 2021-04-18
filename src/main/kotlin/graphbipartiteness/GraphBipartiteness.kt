/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

This algorithm returns whether an input graph is bipartite.
 */

package graphbipartiteness

import graphconnnectivity.GraphConnectivity

class GraphBipartiteness {
    // Number of nodes in the graph
    val n: Int
    // Connectivity of the original graph
    val oriGraph: GraphConnectivity
    // Connectivity of the augmented graph
    val augGraph: GraphConnectivity
    // Random hash seed
    val hashSeed: Int

    // number of node n
    // parameters for sampler: levles, size_recovery, num_hash_recovery
    // parameters for bloomFilter: maxSize_fingerprint, num_hash_fingerprint
    constructor(n: Int,
                levels: Int, size_recovery: Int, num_hash_recovery: Int,
                maxSize_fingerprint: Int, num_hash_fingerprint: Int,
                hashSeed: Int = 100) {
        this.n = n
        this.hashSeed = hashSeed
        oriGraph = GraphConnectivity(n, levels, size_recovery, num_hash_recovery, maxSize_fingerprint, num_hash_fingerprint, hashSeed)
        augGraph = GraphConnectivity(2*n, levels, 2*size_recovery, num_hash_recovery, maxSize_fingerprint, num_hash_fingerprint, hashSeed)
    }

    // Give id of two nodes
    fun update(n1: Int, n2: Int) {
        if (n1 == n2) return
        // Make sure that n1 < n2
        if (n1 > n2) return update(n2, n1)

        oriGraph.update(n1, n2)
        augGraph.update(n1, n2)
        augGraph.update(n1+n, n2+n)
    }

    fun query(): Boolean? {
        val c1 = oriGraph.query()
        val c2 = augGraph.query()
        if (c1 == null || c2 == null) return null

        return c1*2 == c2
    }

    fun merge(summary: GraphBipartiteness) {
        assert(n == summary.n) { "Unable to apply merge, the size of rows is not equal $n != ${summary.n}" }
        assert(hashSeed == summary.hashSeed) { "Unable to apply merge, the hash randomess the same $hashSeed != ${summary.hashSeed}" }

        oriGraph.merge(summary.oriGraph)
        augGraph.merge(summary.augGraph)
    }
}