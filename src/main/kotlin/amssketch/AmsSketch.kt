/*
@author     Yan Song (songyan@iu.edu)
@license    See LICENSE file

AMS Sketch is used to estimate the L_2 norm of the input vector.
This summary can accept both positive and negative weights.
 */

package amssketch

import countsketch.CountSketch
import kotlin.math.pow

class AmsSketch<T> : CountSketch<T> {
    constructor(d: Int, t: Int): super(d, t)

    override fun query(key: T): Double {
        val res = DoubleArray(d, {0.0})
        for (i in 0 until d) {
            for (j in 0 until t) {
                res[i] += counters[i][j].pow(2)
            }
        }
        res.sort()
        return res[d/2]
    }

}