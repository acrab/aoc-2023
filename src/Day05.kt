import com.google.common.truth.Truth

/**
 * Expects the input to be split into separate files for each mapping, e.g. 's2s.txt' for "seed to soil", with headers removed.
 * Files should be placed in the appropriate folder from "Day05/test" or "Day05/real"
 */
fun main() {
    fun map(input: List<Long>, mapping: List<Pair<LongRange, Long>>): List<Long> =
        input.map { start ->
            start + (mapping.firstOrNull { start in it.first }?.second ?: 0)
        }

    val files = listOf("s2s", "s2f", "f2w", "w2l", "l2t", "t2h", "h2l")

    fun part1(input: String): Long {
        var seeds = readSingleLineInput(input + "seeds").split(" ").map { it.toLong() }
        files.forEach { file ->
            val fileData = readInput(input + file).map { line ->
                val (resultStart, inputStart, range) = line.split(" ").map { it.toLong() }
                inputStart..(inputStart + range) to resultStart - inputStart
            }
            seeds = map(seeds, fileData)
        }
        return seeds.min()
    }

    fun part2(input: String): Long {
        var seeds = readSingleLineInput(input + "seeds")
            .split(" ")
            .map { it.toLong() }
            .chunked(2)
            .map { listOf(it[0], it[0] + it[1]) }

        files.forEach { file ->
//            println("Processing $file")
//            println("Starting seeds: $seeds")

            val fileData = readInput(input + file).map { line ->
                val (resultStart, inputStart, range) = line.split(" ").map { it.toLong() }
                inputStart..(inputStart + range) to resultStart - inputStart
            }.sortedBy { it.first.first }

            // Fill in ranges with a "0" offset for the gaps between the defined ranges
            val allRanges = buildList {
                val firstRange = fileData[0].first.first
                if (firstRange > 0) {
                    add(0L..fileData[0].first.first to 0L)
                }
                fileData.windowed(2, 1) {
                    add(it[0])
                    if (it[0].first.last != it[1].first.first) {
                        add(it[0].first.last..it[1].first.first to 0L)
                    }
                }
                val lastRange = fileData.last().first
                val lastOffset = fileData.last().second
                add(lastRange.first..lastRange.last to lastOffset)
            }

            seeds = buildList {
                seeds.forEach { (start, end) ->
//                    println("Mapping range $start..$end")
                    allRanges.forEach { (range, offset) ->
//                        println("  Testing target range $range [+$offset]")
                        if (start in range && end in range) {
//                            println("  Fully covered, new seeds: ${start + offset}..${end + offset}")
                            add(listOf(start + offset, end + offset))
                        } else if (start in range) {
//                            println("  Partially covered, new seeds: ${start + offset}..${range.last}")
                            add(listOf(start + offset, range.last + offset))
                        } else if (end in range) {
//                            println("  Partially covered, new seeds: ${range.first}..${end + offset}")
                            add(listOf(range.first + offset, end + offset))
                        }
                    }
                    val endOfLastRange = allRanges.last().first.last
                    if (start > endOfLastRange) {
//                        println("  Entirely out of range: ${start}..${end}")
                        add(listOf(start, end))
                    } else if (end > endOfLastRange) {
//                        println("  Partially out of range: ${start}..${end}")
                        add(listOf(endOfLastRange, end))
                    }
                }
            }
//            println("Produced seeds: $seeds")
        }
        return seeds.minBy { it.first() }[0]
    }

    val testInput = "Day05/test/"
    Truth.assertThat(part1(testInput)).isEqualTo(35)

    println("-----")
    val input = "Day05/real/"
    part1(input).println()

    println("-----")
    Truth.assertThat(part2(testInput)).isEqualTo(46)
    part2(input).println()
}