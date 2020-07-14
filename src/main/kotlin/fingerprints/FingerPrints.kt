package misc.fingerprints

class FingerPrints<T> {
    private var fingerPrint = 0

    fun update (key: T, weight: Double) {
        return
    }

    fun query (summary: FingerPrints<T>): Boolean {
        return fingerPrint == summary.fingerPrint
    }

    fun merge (summary: FingerPrints<T>) {
        return
    }
}