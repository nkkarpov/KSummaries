package hll

class HyperLL<T> (val maxSize: Int) {

    fun update(key: T) {

    }

    fun query(key: T) {

    }

    fun merge(summary: HyperLL<T>) {
        assert(maxSize == summary.maxSize) { "Unable to apply merge, the sizes are not equal $maxSize != ${summary.maxSize}" }
    }
}

