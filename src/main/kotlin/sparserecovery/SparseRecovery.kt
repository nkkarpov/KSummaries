package sparserecovery

class Cell {
    var count = 0
    var sum = 0.0
}

class SparseRecovery<T> {
    var counters = emptyArray<Cell>().toMutableList()

    constructor(n: Int) {
        for (i in 0 until n) {
            counters.add(Cell())
        }
    }
}