import com.google.common.math.LongMath.pow
import com.google.common.truth.Truth.assertThat
import kotlin.math.max

fun main() {

    data class Node(val name: String, val left: String, val right: String)

    fun String.toNode(): Node {
        val (name, left, right) = """(\w*) = \((\w*), (\w*)\)""".toRegex().matchEntire(this)?.destructured
            ?: error("Input $this did not match pattern!")
        return Node(name, left, right)
    }

    fun part1(input: List<String>): Int {
        val directions = input[0]
        val nodes = input.subList(2, input.size).map(String::toNode).associateBy { it.name }
        var currentNode = nodes["AAA"] ?: error("Unable to find start location")
        var totalSteps = 0
        while (currentNode.name != "ZZZ") {
            currentNode = if (directions[totalSteps % directions.length] == 'L') {
                nodes[currentNode.left] ?: error("Unable to find node called ${currentNode.left}")
            } else {
                nodes[currentNode.right] ?: error("Unable to find node called ${currentNode.right}")
            }
            totalSteps++
        }
        return totalSteps
    }

    fun part2(input: List<String>): Long {
        val directions = input[0]
        val nodes = input.subList(2, input.size).map(String::toNode).associateBy { it.name }

        return nodes.filter { it.key.endsWith("A") }.values
            // work out how long it takes each node's path to loop
            .map {
                var currentStep = 0L
                var currentNode = it
                while (!currentNode.name.endsWith('Z')) {
                    currentNode = if (directions[(currentStep % directions.length).toInt()] == 'L') {
                        nodes[currentNode.left] ?: error("Unable to find node called ${currentNode.left}")
                    } else {
                        nodes[currentNode.right] ?: error("Unable to find node called ${currentNode.right}")
                    }
                    currentStep++
                }
                currentStep
            }
            // Count all the prime factors for each loop
            .map(::primeFactorsOf)
            // Combine the counts: take the largest for each group
            .fold(primes.map { 0 }) { acc: List<Long>, factors: List<Long> ->
                val zippedList = acc.zip(factors, ::max)
                if (zippedList.size < factors.size) {
                    zippedList + factors.subList(zippedList.size, factors.size)
                } else {
                    zippedList + acc.subList(zippedList.size, acc.size)
                }
            }
            // Create the result by multiplying every prime together
            .foldIndexed(1L) { index, acc, primeCount -> acc * pow(primes[index], primeCount.toInt()) }
    }

    val shortTestInput = readInput("Day08_short_test")

    val longTestInput = readInput("Day08_long_test")

    val input = readInput("Day08")

    assertThat(part1(shortTestInput)).isEqualTo(2)
    assertThat(part1(longTestInput)).isEqualTo(6)
    println(part1(input))

    val part2Input = readInput("Day08_part2_test")
    assertThat(part2(part2Input)).isEqualTo(6)
    println(part2(input))
}