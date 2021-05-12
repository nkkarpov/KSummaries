/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

This algorithm answers if the minimum cut of the input graph is greater than a threshold.
 */

package graphmincut

import graphkconnnectivity.GraphKConnectivity
import saltedhash.SaltedHash
import kotlin.math.pow

class GraphMinCut {
    // n: #nodes
    val n: Int
    val levels: Int
    val k: Int

    val hashSeed: Int
    val hash: SaltedHash

    private var kconnectivities: Array<GraphKConnectivity>
    // connectivity parameter: k
    // parameters for sampler: levels, size_recovery, num_hash_recovery
    // parameters for bloomFilter: maxSize_fingerprint, num_hash_fingerprint
    constructor(n: Int, connectivity_levels:Int,
                k: Int, levels: Int, size_recovery: Int, num_hash_recovery: Int,
                maxSize_fingerprint: Int, num_hash_fingerprint: Int,
                hashSeed: Int = 100) {
        this.n = n
        this.k = k
        this.levels = connectivity_levels
        this.hashSeed = hashSeed
        hash = SaltedHash(1, hashSeed)

        kconnectivities = Array(this.levels) { GraphKConnectivity(n, k, levels, size_recovery, num_hash_recovery,
            maxSize_fingerprint, num_hash_fingerprint, hashSeed)}
    }

    fun update(n1: Int, n2: Int, weight: Double = 1.0) {
        if (n1 > n2) return update(n2, n1, weight)
        val edgeIndex = n1*n + n2

        val prob = hash.getIndex(0, edgeIndex.toString(), 2.0.pow(levels-1).toInt())
        for (i in 0 until levels) {
            if (prob < 2.0.pow(levels-1-i).toInt()) {
                kconnectivities[i].update(n1, n2, weight)
            }
        }
    }

    fun query(): Int {
        var last_not_connected_level = 0
        for (i in 0 until levels) {
            // connected at level i
            if (kconnectivities[i].query()) {
                break
            }
            // not connected at level i
            else {
                last_not_connected_level = i
            }
        }

        return (2.0.pow(last_not_connected_level) * k).toInt()
    }

    fun merge(summary: GraphMinCut) {
        assert(n == summary.n) { "Unable to apply merge, the size of graph nodes is not equal $n != ${summary.n}" }
        assert(k == summary.k) { "Unable to apply merge, the connectivity constant is not equal $k != ${summary.k}" }
        assert(levels == summary.levels) { "Unable to apply merge, the connectivity constant is not equal $levels != ${summary.levels}" }
        assert(hashSeed == summary.hashSeed) { "Unable to apply merge, the hash randomness not equal!" }

        for (i in 0 until levels) {
            kconnectivities[i].merge(summary.kconnectivities[i])
        }
    }

}