import filereader.FileReaderBuffer

class FrequencyOracle {
    val hashmap = HashMap<String, Double>()
    private val reader: FileReaderBuffer

    constructor(inputFile: String) {
        reader = FileReaderBuffer(inputFile)
        var word = reader.next()
        while (word != null) {
            val wvec = word.split(" ")
            hashmap[wvec[0]] = wvec[1].toDouble()
            word = reader.next()
        }
    }

    fun checkFrequency(key: String) : Double {
        if (hashmap.containsKey(key)) {
            return hashmap[key]!!
        } else {
            error("Key does not exist. Questionable query!")
        }
    }

    fun merge(fo: FrequencyOracle) {
        fo.hashmap.forEach{
            if (this.hashmap.containsKey(it.key)) {
                this.hashmap[it.key] = this.hashmap[it.key]!! + it.value
            } else {
                this.hashmap[it.key] = it.value
            }
        }
    }
}
