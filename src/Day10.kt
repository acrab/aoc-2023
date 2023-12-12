import com.google.common.truth.Truth.assertThat


fun main() {

    fun followLoop(
        mappedInput: List<MutableList<Cell>>,
        loopAction: (row: Int, column: Int) -> Unit
    ) {
        var row = mappedInput.indexOfFirst { row -> row.any { it is Cell.Start } }
        var column = mappedInput[row].indexOfFirst { it is Cell.Start }
        val firstNeighbours = mappedInput.orthogonalNeighbours(row, column, null)

        // Find first direction
        val startDirection = when {
            firstNeighbours[0]?.connectsToDirection(Compass.SOUTH) == true -> Compass.NORTH
            firstNeighbours[1]?.connectsToDirection(Compass.WEST) == true -> Compass.EAST
            firstNeighbours[2]?.connectsToDirection(Compass.NORTH) == true -> Compass.SOUTH
            firstNeighbours[3]?.connectsToDirection(Compass.EAST) == true -> Compass.WEST
            else -> error("No connections found from start location")
        }
        var currentDirection = startDirection
        println("Starting at [$row, $column], going $currentDirection")
        var returnedToStart = false
        while (!returnedToStart) {
            // Move based on current direction
            when (currentDirection) {
                Compass.NORTH -> row -= 1
                Compass.EAST -> column += 1
                Compass.SOUTH -> row += 1
                Compass.WEST -> column -= 1
            }
            returnedToStart = mappedInput[row][column] is Cell.Start

            // change direction based on where this cell points
            currentDirection = when (mappedInput[row][column]) {
                // Straight lines: continue in current direction
                is Cell.NorthSouth -> currentDirection
                is Cell.EastWest -> currentDirection
                // corners: turn
                is Cell.NorthEast -> if (currentDirection == Compass.WEST) Compass.NORTH else Compass.EAST
                is Cell.NorthWest -> if (currentDirection == Compass.EAST) Compass.NORTH else Compass.WEST
                is Cell.SouthWest -> if (currentDirection == Compass.EAST) Compass.SOUTH else Compass.WEST
                is Cell.SouthEast -> if (currentDirection == Compass.WEST) Compass.SOUTH else Compass.EAST
                // reached beginning again: don't care
                is Cell.Start -> currentDirection
                // Should never happen
                is Cell.Ground -> {
                    println(mappedInput.joinToString("\n") { it.joinToString("") { cell -> cell.symbol } })
                    error("Fell out of pipe at [$row, $column]")
                }
            }

            // Perform the question-specific action at this location
            loopAction(row, column)

        }

        // Replace the start cell with what it actually is!
        // startDirection is the connection out that we used to leave here
        // currentDirection is the direction we travelled to enter here, so the connection must go the other way!
        mappedInput[row][column] = when (startDirection) {
            Compass.NORTH -> when (currentDirection) {
                Compass.NORTH -> Cell.NorthSouth()
                Compass.EAST -> Cell.NorthWest()
                Compass.SOUTH -> error("Bounced back somewhere!")
                Compass.WEST -> Cell.NorthEast()
            }

            Compass.EAST -> when (currentDirection) {
                Compass.NORTH -> Cell.SouthEast()
                Compass.EAST -> Cell.EastWest()
                Compass.SOUTH -> Cell.NorthEast()
                Compass.WEST -> error("Bounced back somewhere")
            }

            Compass.SOUTH -> when (currentDirection) {
                Compass.NORTH -> error("Bounced back somewhere")
                Compass.EAST -> Cell.SouthWest()
                Compass.SOUTH -> Cell.NorthSouth()
                Compass.WEST -> Cell.SouthEast()
            }

            Compass.WEST -> when (currentDirection) {
                Compass.NORTH -> Cell.SouthWest()
                Compass.EAST -> error("Bounced back somewhere")
                Compass.SOUTH -> Cell.NorthWest()
                Compass.WEST -> Cell.EastWest()
            }
        }.also { it.isPartOfLoop = true }
    }

    fun part1(input: List<String>): Long {
        var loopLength = 0
        val loopAction: (row: Int, column: Int) -> Unit = { _, _ -> loopLength++ }
        val mappedInput = input.map { it.map(Cell::fromChar).toMutableList() }
        followLoop(mappedInput, loopAction)

        // Iterate around loop until we reach start again
        return loopLength / 2L
    }

    fun part2(input: List<String>): Int {
        val mappedInput = input.map { it.map(Cell::fromChar).toMutableList() }
        val loopAction: (row: Int, column: Int) -> Unit = { row, column ->
            mappedInput[row][column].isPartOfLoop = true
        }
        followLoop(mappedInput, loopAction)
        println(mappedInput.joinToString("\n") { it.joinToString("") { cell -> cell.symbol } })
        return mappedInput.sumOf { row ->
            var insideLoop = false
            var startCell: Cell? = null
            val x: Int = row.sumOf { cell ->
                if (cell.isPartOfLoop) {
                    when (cell) {
                        is Cell.NorthSouth -> {
                            // Vertical walls always move us from in to out, or vice-versa
                            insideLoop = !insideLoop
                        }

                        // cells with connections east will always be the start of a wall
                        is Cell.NorthEast -> startCell = cell
                        is Cell.SouthEast -> startCell = cell
                        // cells with connections west will always be the end of a wall
                        is Cell.NorthWest -> {
                            if (startCell is Cell.SouthEast) {
                                insideLoop = !insideLoop
                            }
                        }

                        is Cell.SouthWest -> {
                            if (startCell is Cell.NorthEast) {
                                insideLoop = !insideLoop
                            }
                        }

                        is Cell.EastWest -> {
                            // Horizontal walls never move us in or out
                        }

                        is Cell.Ground -> {
                            // We shouldn't get any ground that's part of the loop...
                        }

                        is Cell.Start -> error("This should have been replaced!")
                    }
                    0
                } else {
                    if (insideLoop) {
                        cell.inside = true
                        1
                    } else 0
                } as Int
            }
            println("${row.joinToString("") { if (it.inside) "*" else it.symbol }} has $x inside cells")
            x
        }
    }

    val input = readInput("Day10")
    val testInput = readInput("Day10_test")
    val complexTextInput = readInput("Day10_complexTest")

    assertThat(part1(testInput)).isEqualTo(4)
    assertThat(part1(complexTextInput)).isEqualTo(8)
    println(part1(input))

    val part2TestCases = listOf(
        "Day10_part2_test1" to 4,
        "Day10_part2_test2" to 4,
        "Day10_part2_test3" to 8,
        "Day10_part2_test4" to 10,
    )

    for ((case, result) in part2TestCases) {
        assertThat(part2(readInput(case))).isEqualTo(result)
    }
    println(part2(input))
}

private sealed class Cell(val symbol: String) {
    class NorthSouth : Cell(symbol = "|") {
        override fun connectsToDirection(direction: Compass): Boolean =
            direction == Compass.NORTH || direction == Compass.SOUTH
    }

    class EastWest : Cell("-") {
        override fun connectsToDirection(direction: Compass): Boolean =
            direction == Compass.EAST || direction == Compass.WEST
    }

    class NorthEast : Cell("L") {
        override fun connectsToDirection(direction: Compass): Boolean =
            direction == Compass.EAST || direction == Compass.NORTH
    }

    class NorthWest : Cell("J") {
        override fun connectsToDirection(direction: Compass): Boolean =
            direction == Compass.NORTH || direction == Compass.WEST
    }

    class SouthWest : Cell("7") {
        override fun connectsToDirection(direction: Compass): Boolean =
            direction == Compass.SOUTH || direction == Compass.WEST
    }

    class SouthEast : Cell("F") {
        override fun connectsToDirection(direction: Compass): Boolean =
            direction == Compass.EAST || direction == Compass.SOUTH
    }

    class Ground : Cell(".") {
        override fun connectsToDirection(direction: Compass): Boolean = false
    }

    class Start : Cell("S") {
        override fun connectsToDirection(direction: Compass): Boolean = true
    }

    abstract fun connectsToDirection(direction: Compass): Boolean
    var isPartOfLoop: Boolean = false
    var inside: Boolean = false

    companion object {
        fun fromChar(char: Char) = when (char) {
            '|' -> NorthSouth()
            '-' -> EastWest()
            'L' -> NorthEast()
            'J' -> NorthWest()
            '7' -> SouthWest()
            'F' -> SouthEast()
            '.' -> Ground()
            'S' -> Start()
            else -> error("Unexpected char $char")
        }
    }
}
