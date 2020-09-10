package frequencies.tests

import FrequencyOracle
import countmin.CountMin
import filereader.FileReaderBuffer
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.random.Random.Default.nextInt
import kotlin.test.assertFails

class CountMinTests {
    @Test
    fun testInit() {
        val weight = 0.1
        val sketch = CountMin<Int>(10, 100)
        for (i in 0 until 10000) {
            sketch.update(nextInt(), weight)
        }
        sketch.query(nextInt())
        sketch.query(nextInt())
    }

    @Test
    fun testMergeFail() {
        val d = 10
        val t = 100
        val a = CountMin<Int>(d, t)
        val b = CountMin<Int>(d, t, 20, 20)
        val weight = 0.1
        for (i in 0 until 10000) {
            val item = nextInt()
            a.update(nextInt(), weight)
            b.update(nextInt(), weight)
        }
        assertFails("Invalid merge") { a.merge(b) }
    }

    // Tolerate is epsilon*total_weight
    fun reportErrorRate(cm: CountMin<String>, fo: FrequencyOracle, epsilon: Double) : Double{
        // Compute for tolerance
        var totalWeight = 0.0
        fo.hashmap.forEach{
            totalWeight += it.value
        }
        val tolerance = epsilon * totalWeight

        var total = 0
        var correct_count = 0
        fo.hashmap.forEach{
            val item = it.key
            val estimate = cm.query(item)
            val trueValue = it.value
            val difference = (estimate-trueValue).absoluteValue
//            println("Item: $item, Estimate: $estimate, True: $trueValue, Difference: $difference")
            if (difference < tolerance) {
                correct_count++
            }
            total++
        }
        assert(total == fo.hashmap.size) {"Wrong in hashmap iteration"}

        return (1 - (correct_count.toDouble() / total.toDouble()))
    }

    fun testFiles(fileName1: String, fileName2: String, d: Int, t: Int, epsilon: Double = 1e-3) {
        // Load ground truth
        val fo1 = FrequencyOracle(fileName1+"_frequency.txt")
        val fo2 = FrequencyOracle(fileName2+"_frequency.txt")

        // Load input files
        val reader1 = FileReaderBuffer(fileName1+".txt")
        val reader2 = FileReaderBuffer(fileName2+".txt")
        // Create data structures
        val cm1 = CountMin<String>(d, t)
        val cm2 = CountMin<String>(d, t)
        // Feed input into data structures
        var word: String?
        word = reader1.next()
        while (word != null) {
            cm1.update(word)
            word = reader1.next()
        }
        word = reader2.next()
        while (word != null) {
            cm2.update(word)
            word = reader2.next()
        }

        // Check for performance
        val errorRate1 = reportErrorRate(cm1, fo1, epsilon)
        println(("Error percentage on " + fileName1 + " dataset with %d rows and %d cols " +
                "is %.2f %%").format(d, t, errorRate1*100))

        val errorRate2 = reportErrorRate(cm2, fo2, epsilon)
        println(("Error percentage on " + fileName2 + " dataset with %d rows and %d cols " +
                "is %.2f %%").format(d, t, errorRate2*100))

        // Merge
        cm1.merge(cm2)
        fo1.merge(fo2)
        val errorRateMerged = reportErrorRate(cm1, fo1, epsilon)
        println(("Error rate on" + fileName1 + " and " + fileName2
                + "merged dataset with %d rows and %d cols " +
                "is %.2f %%").format(d, t, errorRateMerged*100))
    }

    fun smallTest() {
        val fileNameT1 = "data/test1"
        val fileNameT2 = "data/test2"
        testFiles(fileNameT1, fileNameT2, 5, 1000)
    }

    @Test
    fun testMergeT1T2() {
        val fileNameT1 = "data/T10I4D100K"
        val fileNameT2 = "data/T40I10D100K"
        testFiles(fileNameT1, fileNameT2, 5, 1000)
    }

    @Test
    fun testMergeT2K() {
        val fileNameT2 = "data/T40I10D100K"
        val fileNameK = "data/kosarak"
        testFiles(fileNameT2, fileNameK, 5, 1000)
    }

    @Test
    fun testMergeN1N2() {
        val fileNameN1 = "data/n1"
        val fileNameN2 = "data/n2"
        testFiles(fileNameN1, fileNameN2, 5, 10000, 1e-4)
    }
}