package fingerprints

import java.security.MessageDigest
import kotlin.math.pow
import kotlin.random.Random.Default.nextInt

class FingerPrints<T> {
    private var fingerPrint = 0
    private val p = 7368787
    private var alpha = 0
    private val md = MessageDigest.getInstance("SHA-256")

    init {
        alpha = nextInt(p)
    }

    fun update (key: T, weight: Double) {
        val hashcode = key.hashCode().toString().toByteArray()
        val hash = getHash(md.digest(hashcode))
        // Update fingerprint
        fingerPrint += ((alpha*weight).pow(hash)).toInt()
        fingerPrint = fingerPrint.rem(p)
    }

    fun query (summary: FingerPrints<T>): Boolean {
        return fingerPrint == summary.fingerPrint
    }

    fun merge (summary: FingerPrints<T>) {
        // merge both summaries
        val newPrint = (fingerPrint + summary.fingerPrint).rem(p)
        fingerPrint = newPrint
        summary.fingerPrint = newPrint
    }

    private fun getHash(bytes: ByteArray) : Int {
        var result = 0
        var shift = 0
        for (i in 0 until 8) {
            result = result or (bytes.get(i).toInt() shl shift)
            shift += 8
        }
        return result
    }
}
