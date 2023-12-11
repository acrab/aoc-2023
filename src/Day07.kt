import com.google.common.truth.Truth.assertThat

fun main() {

    data class Hand(val cardString: String, val bet: Long) {
        val rank: Rank

        init {
            val cardTypes = cardString.associateBy { it }
            rank = when (cardTypes.size) {
                // One type of card means they're all the same (five of a kind)
                1 -> Rank.FiveOfAKind
                // Two types of card is either: ABBBB (4 of a kind) or AABBB (full house).
                2 -> if (cardTypes.any { card -> cardString.count { it == card.key } == 4 }) Rank.FourOfAKind else Rank.FullHouse
                // Three types of card is either: ABCCC (three of a kind) or ABBCC (two pair).
                3 -> if (cardTypes.any { card -> cardString.count { it == card.key } == 3 }) Rank.ThreeOfAKind else Rank.TwoPair
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
                    'J' -> 11
                    'T' -> 10
                    else -> error("Unexpected rank $it")
                }
            }
        }

        fun compareTo(other: Hand): Int =
            if (other.rank == this.rank) {
                // compare cards
                cardList.zip(other.cardList)
                    .fold(0) { acc, pair -> if (acc == 0) pair.first.compareTo(pair.second) else acc }
            } else {
                // compare ranks
                other.rank.compareTo(this.rank)
            }


    }

    fun part1(input: List<String>): Long = input
        .map {
            val (cards, bet) = it.split(" ")
            Hand(cards, bet.toLong())
        }
        .sortedWith(Hand::compareTo)
        .mapIndexed { index, hand -> hand.bet * (index + 1) }
        .sum()


    data class HandWithJokers(val cardString: String, val bet: Long) {
        var rank: Rank
            private set

        init {
            val cardTypes = mutableMapOf<Char, Int>()
            cardString.forEach { card ->
                cardTypes[card] = cardString.count { it == card }
            }
            val numJokers = cardTypes['J'] ?: 0
            cardTypes.remove('J')
            if (cardTypes.isEmpty()) {
                cardTypes['J'] = numJokers
            } else {
                val maxCount = cardTypes.maxBy { it.value }
                cardTypes[maxCount.key] = maxCount.value + numJokers
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
                    'T' -> 10
                    // 'J' is now a joker, and is the least valued card
                    'J' -> 1
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

    fun part2(input: List<String>) = input
        .map {
            val (cards, bet) = it.split(" ")
            HandWithJokers(cards, bet.toLong())
        }
        .sortedWith(HandWithJokers::compareTo)
        .mapIndexed { index, hand ->
            val strength = index + 1
            val score = hand.bet * strength
            println("Given ${hand.cardString} (${hand.rank}, $${hand.bet}) rank $strength, scoring $score")
            score
        }
        .sum()

    val testInput = readInput("Day07_test")
    assertThat(part1(testInput)).isEqualTo(6440L)

    println("-----")
    val input = readInput("Day07")
    part1(input).println()

    println("-----")
    assertThat(part2(testInput)).isEqualTo(5905)
    println("-----")
    part2(input).println()
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