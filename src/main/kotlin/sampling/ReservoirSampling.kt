package sampling

import kotlin.math.min
import kotlin.random.Random

class ReservoirSampling<T>(val sampleSize: Int) {
    var size = 0
    private val rnd = Random(System.nanoTime())
    private val pull = mutableListOf<T>()

    fun sample() = pull.toList()
    fun update(x: T) {
        if (size < sampleSize) {
            size++
            pull.add(x)
            val i = rnd.nextInt(size)
            pull[size - 1] = pull[i].also { pull[i] = pull[size - 1] }
        } else {
            size++
            val i = rnd.nextInt(size)
            if (i < sampleSize) pull[i] = x
        }
    }

    fun merge(summary: ReservoirSampling<T>) {
        assert(sampleSize == summary.sampleSize) {
            "Unable to apply merge, the sample sizes are not equal $sampleSize != ${summary.sampleSize}"
        }
        val a = this.sample()
        val b = summary.sample()
        val result = mutableListOf<T>()
        var ka = 0
        var kb = 0
        var na = size
        var nb = summary.size
        for (i in 0 until min(sampleSize, size + summary.size)) {
            val j = rnd.nextInt(na + nb)
            if (j < na) {
                result.add(a[ka])
                ka++
                na--
            } else {
                result.add(b[kb])
                kb++
                nb--
            }
        }
        size += summary.size
        pull.clear()
        for (x in result) pull.add(x)
    }
}