import com.google.common.truth.Truth

fun main() {

    fun List<Long>.findNextNumber(): Long =
        if (all { it == 0L }) 0
        else zipWithNext { a, b -> b - a }.findNextNumber() + last()

    fun List<Long>.findPreviousNumber(): Long =
        if (all { it == 0L }) 0
        else first() - zipWithNext { a, b -> b - a }.findPreviousNumber()

    fun execute(input: List<String>, action: List<Long>.() -> Long): Long =
        input.sumOf { line ->
            line
                .split(" ")
                .map(String::toLong)
                .action()
        }

    val input = readInput("Day09")
    val testInput = readInput("Day09_test")

    Truth.assertThat(execute(testInput, List<Long>::findNextNumber)).isEqualTo(114)
    println(execute(input, List<Long>::findNextNumber))

    Truth.assertThat(execute(testInput, List<Long>::findPreviousNumber)).isEqualTo(2)
    println(execute(input, List<Long>::findPreviousNumber))
}