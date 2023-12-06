import com.google.common.truth.Truth.assertThat

fun main() {
    fun part1(input: List<String>): Long {
        val (times, distances) = input.map { it.split(Regex("""\s+""")).drop(1).map(String::toInt) }

        return times.mapIndexed { index, raceTime ->
            println("Race time: $raceTime")
            val result = (1 until raceTime).count { holdTime ->
                println("  holding for $holdTime")
                val distance = holdTime * (raceTime - holdTime)
                val record = distances[index]
                println("    travelled $distance against record of $record")
                distance > record
            }
            println("Won $result times")
            result
        }.fold(1) { l, r -> l * r }
    }

    fun part2(input: List<String>): Int {
        val (raceTime, record) = input.map { it.split(Regex("""\s+""")).drop(1).joinToString(separator = "").toLong() }

        println("Race time: $raceTime")
        val result = (1 until raceTime).count { holdTime ->
            val distance = holdTime * (raceTime - holdTime)
            distance > record
        }
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