import com.google.common.truth.Truth.assertThat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun calculateDistances(input: List<String>, expansionAmount: Long): Long {
        // get a list of all star coordinates
        val stars = input.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { index: Int, c: Char ->
                if (c == '#') rowIndex to index else null
            }
        }
        // get a list of all blank rows
        val blankRows = input.mapIndexedNotNull { index: Int, s: String -> if (s.all { it == '.' }) index else null }
        // get a list of all blank columns
        val blankColumns = (0 until input[0].length).mapNotNull { i ->
            if (input.all { it[i] == '.' }) i else null
        }
        val distances = stars.flatMapIndexed { index: Int, star: Pair<Int, Int> ->
            val targets = stars.subList(index + 1, stars.size)
            targets.map { target ->
                val startRow = min(star.first, target.first)
                val endRow = max(star.first, target.first)
                // find manhattan distance between stars
                // add number of blank rows/columns crossed
                val deltaRows = abs(startRow - endRow) + (blankRows.count { it in startRow..endRow } * expansionAmount)
                val startColumn = min(star.second, target.second)
                val endColumn = max(star.second, target.second)
                val deltaColumns =
                    abs(startColumn - endColumn) + (blankColumns.count { it in startColumn..endColumn } * expansionAmount)

                deltaColumns + deltaRows
            }
        }
        return distances.sum()
    }

    fun part1(input: List<String>): Long = calculateDistances(input, 1L)

    fun part2(input: List<String>): Long = calculateDistances(input, 999_999L)

    val testInput = readInput("Day11_test")
    assertThat(calculateDistances(testInput, 1L)).isEqualTo(374)

    println("-----")
    val input = readInput("Day11")
    part1(input).println()

    println("-----")
    assertThat(calculateDistances(testInput, 9L)).isEqualTo(1030)
    assertThat(calculateDistances(testInput, 99L)).isEqualTo(8410)
    part2(input).println()
}