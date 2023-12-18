import com.google.common.truth.Truth.assertThat
import kotlin.math.max

fun main() {
    open class GridSquare {
        var north: GridSquare? = null
        var east: GridSquare? = null
        var west: GridSquare? = null
        var south: GridSquare? = null
        var row: Int = 0
        var column: Int = 0

        var energisedFromNorth = false
        var energisedFromSouth = false
        var energisedFromEast = false
        var energisedFromWest = false

        val energised
            get() = energisedFromWest || energisedFromSouth || energisedFromNorth || energisedFromEast

        fun resetEnergisedState() {
            energisedFromNorth = false
            energisedFromSouth = false
            energisedFromEast = false
            energisedFromWest = false
        }


        fun energiseFromNorth() {
            if (!energisedFromNorth) {
                energisedFromNorth = true
                transmitBeamFromNorth()
            }
        }

        open fun transmitBeamFromNorth() {
            south?.energiseFromNorth()
        }

        fun energiseFromSouth() {
            if (!energisedFromSouth) {
                energisedFromSouth = true
                transmitBeamFromSouth()
            }
        }

        open fun transmitBeamFromSouth() {
            north?.energiseFromSouth()
        }

        fun energiseFromEast() {
            if (!energisedFromEast) {
                energisedFromEast = true
                transmitBeamFromEast()
            }
        }

        open fun transmitBeamFromEast() {
            west?.energiseFromEast()
        }

        fun energiseFromWest() {
            if (!energisedFromWest) {
                energisedFromWest = true
                transmitBeamFromWest()
            }
        }

        open fun transmitBeamFromWest() {
            east?.energiseFromWest()
        }

        override fun toString(): String = if (energised) "#" else "."
    }

    // '/'
    class SWtoNEMirror : GridSquare() {
        override fun transmitBeamFromNorth() {
            west?.energiseFromEast()
        }

        override fun transmitBeamFromSouth() {
            east?.energiseFromWest()
        }

        override fun transmitBeamFromEast() {
            south?.energiseFromNorth()
        }

        override fun transmitBeamFromWest() {
            north?.energiseFromSouth()
        }

        override fun toString(): String = "/"
    }

    // '\'
    class NWtoSEMirror : GridSquare() {
        override fun transmitBeamFromNorth() {
            east?.energiseFromWest()
        }

        override fun transmitBeamFromSouth() {
            west?.energiseFromEast()
        }

        override fun transmitBeamFromEast() {
            north?.energiseFromSouth()
        }

        override fun transmitBeamFromWest() {
            south?.energiseFromNorth()
        }

        override fun toString(): String = "\\"
    }

    // '-'
    class HorizontalSplitter : GridSquare() {
        override fun transmitBeamFromNorth() {
            east?.energiseFromWest()
            west?.energiseFromEast()
        }

        override fun transmitBeamFromSouth() {
            east?.energiseFromWest()
            west?.energiseFromEast()
        }

        override fun toString(): String = "-"
    }

    // '|'
    class VerticalSplitter : GridSquare() {
        override fun transmitBeamFromEast() {
            north?.energiseFromSouth()
            south?.energiseFromNorth()
        }

        override fun transmitBeamFromWest() {
            north?.energiseFromSouth()
            south?.energiseFromNorth()
        }

        override fun toString(): String = "|"
    }

    fun Char.toGridSquare() =
        when (this) {
            '.' -> GridSquare()
            '\\' -> NWtoSEMirror()
            '/' -> SWtoNEMirror()
            '|' -> VerticalSplitter()
            '-' -> HorizontalSplitter()
            else -> error("Unexpected grid square: $this")
        }

    fun parseMap(input: List<String>): List<List<GridSquare>> {
        val map = input.map { it.map(Char::toGridSquare) }
        map.forEachIndexed { verticalCoordinate, row ->
            row.forEachIndexed { horizontalCoordinate, gridSquare ->
                gridSquare.row = verticalCoordinate
                gridSquare.column = horizontalCoordinate
                gridSquare.north = map.getOrNull(verticalCoordinate - 1)?.getOrNull(horizontalCoordinate)
                gridSquare.south = map.getOrNull(verticalCoordinate + 1)?.getOrNull(horizontalCoordinate)
                gridSquare.east = row.getOrNull(horizontalCoordinate + 1)
                gridSquare.west = row.getOrNull(horizontalCoordinate - 1)
            }
        }
        return map
    }


    fun energise(map: List<List<GridSquare>>, x: Int, y: Int, initial: GridSquare.() -> Unit): Int {
        map[x][y].initial()
        return map.sumOf { row -> row.count { it.energised } }
    }

    fun part1(input: List<String>) = energise(parseMap(input), 0, 0, GridSquare::energiseFromWest)

    fun List<List<GridSquare>>.resetMap() = forEach { row -> row.forEach(GridSquare::resetEnergisedState) }

    fun part2(input: List<String>): Int {
        var best = 0
        val map = parseMap(input)
        val height = map.size - 1
        val width = map[0].size - 1
        for (i in map.indices) {
            best = max(best, energise(map, i, 0, GridSquare::energiseFromWest))
            map.resetMap()
            best = max(best, energise(map, i, width, GridSquare::energiseFromEast))
            map.resetMap()
        }

        for (i in map[0].indices) {
            best = max(best, energise(map, 0, i, GridSquare::energiseFromNorth))
            map.resetMap()
            best = max(best, energise(map, height, i, GridSquare::energiseFromSouth))
            map.resetMap()
        }

        return best
    }

    val testInput = readInput("Day16_test")
    assertThat(part1(testInput)).isEqualTo(46)

    println("-----")
    val input = readInput("Day16")
    part1(input).println()

    println("-----")
    assertThat(part2(testInput)).isEqualTo(51)
    part2(input).println()
}