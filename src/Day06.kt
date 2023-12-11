import com.google.common.truth.Truth.assertThat
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    /**
     * In a race of duration raceTime, when the button is held for holdTime milliseconds, the distance travelled is calculated as
     *
     *     distance = holdTime(raceTime-holdTime)
     *
     * To find the minimum/maximum time required to reach the record, we can rearrange this into a quadratic equation
     *
     *     -1*holdTime^2 + raceTime*holdTime - record = 0
     *
     * then solve using the quadratic formula
     */
    fun calculateNumberOfWaysToBeatRecord(raceTime: Long, record: Long): Long {

        val (maxTime, minTime) = quadraticFormula(-1.0, raceTime.toDouble(), -1.0 * record.toDouble())
        return (ceil(maxTime) - floor(minTime) - 1).toLong()
    }

    fun part1(input: List<String>): Long {
        val (times, distances) = input.map { it.split(Regex("""\s+""")).drop(1).map(String::toLong) }

        return times.foldIndexed(1) { index, acc, raceTime -> acc * calculateNumberOfWaysToBeatRecord(raceTime, distances[index]) }
    }

    fun part2(input: List<String>): Long {
        val (raceTime, record) = input.map { it.split(Regex("""\s+""")).drop(1).joinToString(separator = "").toLong() }

        println("Race time: $raceTime")
        val result = calculateNumberOfWaysToBeatRecord(raceTime, record)
        println("Won $result times")
        return result
    }

    val testInput = readInput("Day06_test")
    assertThat(part1(testInput)).isEqualTo(288)

    println("-----")
    val input = readInput("Day06")
    part1(input).println()

    println("-----")
    assertThat(part2(testInput)).isEqualTo(71503)
    part2(input).println()
}