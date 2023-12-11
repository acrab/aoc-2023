import com.google.common.truth.Truth.assertThat

fun main() {

    data class HandWithJokers(val cardString: String, val bet: Long, val withJokers: Boolean) {
        var rank: Rank
            private set

        init {
            val cardTypes = mutableMapOf<Char, Int>()
            cardString.forEach { card ->
                cardTypes[card] = cardString.count { it == card }
            }

            // If we're using jokers, turn them all into the card with the highest count
            if (withJokers) {
                val numJokers = cardTypes['J'] ?: 0
                // Modify the hand unless it's all jokers
                if (numJokers != 5) {
                    cardTypes.remove('J')
                    val maxCount = cardTypes.maxBy { it.value }
                    cardTypes[maxCount.key] = maxCount.value + numJokers
                }
            }

            rank = when (cardTypes.size) {
                // One type of card means they're all the same (five of a kind)
                1 -> Rank.FiveOfAKind
                // Two types of card is either: ABBBB (4 of a kind) or AABBB (full house).
                2 -> if (cardTypes.any { card -> card.value == 4 }) Rank.FourOfAKind else Rank.FullHouse
                // Three types of card is either: ABCCC (three of a kind) or ABBCC (two pair).
                3 -> if (cardTypes.any { card -> card.value == 3 }) Rank.ThreeOfAKind else Rank.TwoPair
                // Four types of card must be ABCDD (one pair)
                4 -> Rank.OnePair
                // 5 types of card means all are different (high card)
                5 -> Rank.HighCard
                else -> error("Unexpected number of unique cards $cardTypes in $cardString")
            }
        }

        val cardList: List<Int> = cardString.map {
            if (it.isDigit()) {
                it.digitToInt()
            } else {
                when (it) {
                    'A' -> 14
                    'K' -> 13
                    'Q' -> 12
                    // Jokers are the least valuable card
                    'J' -> if (withJokers) 1 else 11
                    'T' -> 10
                    else -> error("Unexpected rank $it")
                }
            }
        }

        fun compareTo(other: HandWithJokers): Int =
            if (other.rank == this.rank) {
                // compare cards
                cardList.zip(other.cardList)
                    .fold(0) { acc, pair -> if (acc == 0) pair.first.compareTo(pair.second) else acc }
            } else {
                // compare ranks
                other.rank.compareTo(this.rank)
            }
    }

    fun scoreHands(input: List<String>, withJokers: Boolean): Long = input
        .map {
            val (cards, bet) = it.split(" ")
            HandWithJokers(cards, bet.toLong(), withJokers)
        }
        .sortedWith(HandWithJokers::compareTo)
        .mapIndexed { index, hand -> hand.bet * (index + 1) }
        .sum()

    val testInput = readInput("Day07_test")
    assertThat(scoreHands(testInput, withJokers = false)).isEqualTo(6440L)

    println("-----")
    val input = readInput("Day07")
    scoreHands(input, withJokers = false).println()

    println("-----")
    assertThat(scoreHands(testInput, withJokers = true)).isEqualTo(5905)
    scoreHands(input, withJokers = true).println()
}

private enum class Rank {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPair,
    OnePair,
    HighCard
}