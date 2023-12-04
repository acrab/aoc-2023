import com.google.common.truth.Truth.assertThat
import kotlin.math.pow

fun main() {
    fun parseCard(it: String) = it.split(": ")[1].split(" | ").map { it.split(" ").filter { it.isNotBlank() } }

    fun part1(input: List<String>): Double =
        input.sumOf {
            val (winningNumbers, game) = parseCard(it)
            val matches = game.count { it in winningNumbers }
            if (matches > 0) 2.0.pow(matches - 1) else 0.0
        }

    fun part2(input: List<String>): Long {
        data class Card(val winningNumbers: List<String>, val gameNumbers: List<String>, var copies: Long)

        val cards = input.map {
            val (winningNumbers, game) = parseCard(it)
            Card(winningNumbers, game, 1)
        }

        cards.forEachIndexed { i, card ->
            println("Card ${i + 1}: ${card.winningNumbers} | ${card.gameNumbers} x ${card.copies}")
            val points = card.gameNumbers.count { it in card.winningNumbers }
            println("scores $points")
            if (points > 0) {
                for (x in i+1..i + points) {
                    println("Adding ${card.copies} of card #$x")
                    cards[x].copies += card.copies
                }
            }
        }

        return cards.sumOf { it.copies }
    }

    val testInput = readInput("Day04_test")
    assertThat(part1(testInput)).isEqualTo(13)

    println("-----")
    val input = readInput("Day04")
    part1(input).println()

    println("-----")
    assertThat(part2(testInput)).isEqualTo(30)
    part2(input).println()
}