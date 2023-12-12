import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.sqrt

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Reads lines from the given input txt file.
 */
fun readSingleLineInput(name: String) = readInput(name).first()


/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun quadraticFormula(a: Double, b: Double, c: Double): Pair<Double, Double> {
    val denominator = 2 * a
    val discriminant = sqrt(b * b - (4 * a * c))
    val frontBit = -1 * b
    return (frontBit - discriminant) / denominator to (frontBit + discriminant) / denominator
}

var primes = mutableListOf(2L)

fun findPrimesLessThan(value: Long) {
    for (i in 2..value) {
        if (primes.all { i % it != 0L }) {
            primes.add((i))
        }
    }
}

fun primeFactorsOf(value: Long): List<Long> {
    // prefill the primes list
    findPrimesLessThan(value)
    return primes.map {
        var count = 0L
        var current = value
        while (current % it == 0L) {
            count++
            current /= it
        }
        count
    }
}

fun <T> List<List<T>>.orthogonalNeighbours(x: Int, y: Int, default: T): List<T> {
    val north = getOrNull(x - 1)?.get(y) ?: default
    val south = getOrNull(x + 1)?.get(y) ?: default
    val west = this[x].getOrElse(y - 1) { default }
    val east = this[x].getOrElse(y + 1) { default }
    return listOf(north, east, south, west)
}

enum class Compass {
    NORTH,
    EAST,
    SOUTH,
    WEST
}