import com.google.common.truth.Truth.assertThat
import java.math.BigInteger
import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int = input.mapIndexedNotNull { index, s ->
        val maxRed = 12
        val maxGreen = 13
        val maxBlue = 14
        val games = s.split(": ")[1].split("; ")
        println("Testing $index: $s")
        val result = games.all { game ->
            game.split(", ").none { cubeGroup ->
                println("Testing group $cubeGroup")
                val (amountString, colour) = cubeGroup.split(" ")
                val amount = amountString.toInt()
                when (colour) {
                    "red" -> amount > maxRed
                    "green" -> amount > maxGreen
                    "blue" -> amount > maxBlue
                    else -> error("Unexpected colour: $colour")
                }
            }
        }
        if (result) {
            println("adding $index")
            index + 1
        } else {
            println("adding nothing")
            null
        }
    }.sum()


    fun part2(input: List<String>): BigInteger = input.fold(BigInteger.ZERO) { acc, s ->
        var maxRed = 0L
        var maxGreen = 0L
        var maxBlue = 0L
        s.split(": ")[1].split("; ").forEach { game ->
            game.split(", ").forEach { cubeGroup ->
                val (amountString, colour) = cubeGroup.split(" ")
                val amount = amountString.toLong()
                when (colour) {
                    "red" -> maxRed = max(amount, maxRed)
                    "green" -> maxGreen = max(amount, maxGreen)
                    "blue" -> maxBlue = max(amount, maxBlue)
                    else -> error("Unexpected colour: $colour")
                }
            }
        }
        acc + BigInteger.valueOf(maxRed * maxGreen * maxBlue)
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")

    assertThat(part1(testInput)).isEqualTo(8)
    println("Part1 test passed")

    val input = readInput("Day02")

    part1(input).println()

    println("-----")

    assertThat(part2(testInput)).isEqualTo(BigInteger.valueOf(2286))
    println("Part2 test passed")

    part2(input).println()
}
