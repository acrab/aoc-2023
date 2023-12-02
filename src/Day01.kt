import com.google.common.truth.Truth.assertThat
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int = input.sumOf { line ->
        "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt()
    }

    val numbers = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    )

//    fun part2(input: List<String>): Int = input.sumOf { line ->
//        val firstDigitString = numbers.asIterable().fold(line) { processed, item -> processed.replace(item.key, item.value) }.first{it.isDigit()}
//        println("Processed $line into $firstDigitString")
//        val lastDigitString = numbers.asIterable().fold(line.reversed()) { processed, item -> processed.replace(item.key.reversed(), item.value) }.first{it.isDigit()}
//        println("Processed $line into $lastDigitString")
//        "$firstDigitString$lastDigitString".toInt()
//    }

    fun part2(input: List<String>): Int = input.sumOf { line ->
        val firstDigit = numbers.minBy { number ->
            var stringPosition = line.indexOf(number.key)
            if (stringPosition == -1) stringPosition = line.length + 1
            var digitPosition = line.indexOf(number.value)
            if (digitPosition == -1) digitPosition = line.length + 1
            min(stringPosition, digitPosition)
        }.value.toInt() * 10
        val secondDigit =
            numbers.maxBy { number ->
                val stringPosition = line.lastIndexOf(number.key)
                val digitPosition = line.lastIndexOf(number.value)
                max(stringPosition, digitPosition)
            }.value.toInt()
        firstDigit + secondDigit
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    println("-----")
    val input = readInput("Day01")
    part1(input).println()
    println("-----")
    assertThat(part2(testInput)).isEqualTo(142)
    println("-----")
    val testInputTwo = readInput("Day01_test_2")
    assertThat(part2(testInputTwo)).isEqualTo(281)
    println("-----")
    part2(input).println()
}
