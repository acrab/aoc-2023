import com.google.common.truth.Truth.assertThat
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun checkForSymbolsAdjacentTo(row: Int, start: Int, end: Int, input: List<String>): Boolean {
        //test our row
        val targetRow = input[row]
        if (start > 0 && targetRow[start - 1] != '.') return true
        if (end < targetRow.length - 1 && targetRow[end + 1] != '.') return true
        // test row above
        if (row > 0) {
            val rowAbove = input[row - 1]
            val sectionAbove = rowAbove.substring(max(start - 1, 0), min(end + 2, rowAbove.length - 1))
            if (sectionAbove.any { it != '.' && !it.isDigit() }) return true
        }
        // test row below
        if (row < input.size - 1) {
            val rowBelow = input[row + 1]
            val sectionBelow = rowBelow.substring(max(start - 1, 0), min(end + 2, rowBelow.length - 1))
            if (sectionBelow.any { it != '.' && !it.isDigit() }) return true
        }
        // else, not valid
        return false
    }

    fun part1(input: List<String>): Long =
        input.mapIndexed { index, s ->
            Regex("""\d+""").findAll(s).sumOf {
                if (checkForSymbolsAdjacentTo(
                        index,
                        it.range.first,
                        it.range.last,
                        input
                    )
                ) it.value.toLong() else 0
            }
        }.sum()

    fun findStart(possibleStart: Int, row: String): Int {
        var possibleStart1 = possibleStart
        while (possibleStart1 > 0 && row[possibleStart1 - 1].isDigit()) {
            possibleStart1--
        }
        return possibleStart1
    }

    fun findEnd(end: Int, row: String): Int {
        var end1 = end
        while (end1 < row.length - 1 && row[end1 + 1].isDigit()) {
            end1++
        }
        return end1
    }

    fun findNumbersNear(position: Int, row: String): List<Long> {
        println("finding numbers near $position in $row")
        // Check at position
        return if (row[position].isDigit()) {
            println("Finding number around target")
            val start = findStart(position, row)
            println("start is $start")
            val end = findEnd(position, row)
            println("end is $end")
            val part = row.substring(start, end + 1)
            println("found substring: $part")
            listOf(part.toLong())
        } else {
            buildList {
                if (position > 0 && row[position - 1].isDigit()) {
                    println("Finding number before target")
                    val start = findStart(position - 1, row)
                    println("start is $start")
                    val end = position - 1
                    println("end is $end")
                    val part = row.substring(start, end + 1)
                    println("found substring: $part")
                    add(part.toLong())
                }

                if (position < row.length - 1 && row[position + 1].isDigit()) {
                    println("Finding number after target")
                    val start = position + 1
                    println("start is $start")
                    val end = findEnd(position + 1, row)
                    println("end is $end")
                    val part = row.substring(start, end + 1)
                    println("found substring: $part")
                    add(part.toLong())

                    row.substring(position + 1, findEnd(position + 1, row)+1).toLong()
                }
            }
        }
    }

    fun part2(input: List<String>): Long = input.mapIndexed { index, s ->
        Regex("""\*""").findAll(s).sumOf {
            val adjacentNumbers = buildList {
                if (index > 0) addAll(findNumbersNear(it.range.first, input[index - 1]))
                addAll(findNumbersNear(it.range.first, s))
                if (index < input.size - 1) addAll(
                    findNumbersNear(
                        it.range.first,
                        input[index + 1]
                    )
                )
            }
            if (adjacentNumbers.size == 2) {
                adjacentNumbers[0] * adjacentNumbers[1]
            } else {
                0
            }
        }
    }.sum()

    val testInput = readInput("Day03_test")
    assertThat(part1(testInput)).isEqualTo(4361)

    println("-----")
    val input = readInput("Day03")
    part1(input).println()

    println("-----")
    assertThat(part2(testInput)).isEqualTo(467835)
    part2(input).println()
}