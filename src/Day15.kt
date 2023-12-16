import com.google.common.truth.Truth.assertThat

fun main() {
    fun hash(s: String) = s.fold(0) { acc: Int, c: Char ->
        ((acc + c.code) * 17) % 256
    }

    fun part1(input: String): Int =
        input
            .split(",")
            .sumOf(::hash)

    fun part2(input: String): Long {
        val boxes = List<MutableList<Pair<String, String>>>(256) { mutableListOf() }
        val lensRegex = Regex("""^(\w*)([-=])(\d*)$""")
        input
            .split(",")
            .forEach { command ->
                val (lens, instruction, operand) = lensRegex.matchEntire(command)?.destructured
                    ?: error("Unable to parse $command")

                val boxNumber = hash(lens)
                val box = boxes[boxNumber]
                if (instruction == "-") {
                    box.removeIf { it.first == lens }
                } else {
                    val index = box.indexOfFirst { it.first == lens }
                    if (index != -1) {
                        box[index] = lens to operand
                    } else {
                        box.add(lens to operand)
                    }
                }
            }

        return boxes.foldIndexed(0L) { boxNumber, outerAcc, lenses ->
            lenses.foldIndexed(outerAcc) { lensNumber, acc, (_, focalLength) ->
                acc + ((1 + boxNumber) * (1 + lensNumber) * focalLength.toInt())
            }
        }
    }

    val testInput = readSingleLineInput("Day15_test")
    val input = readSingleLineInput("Day15")

    assertThat(part1(testInput)).isEqualTo(1320)
    println(part1(input))

    assertThat(part2(testInput)).isEqualTo(145)
    println(part2(input))
}