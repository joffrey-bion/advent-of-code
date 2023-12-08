package org.hildan.aoc.day8

import org.hildan.aoc.client.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 8)
    println(part1(inputLines)) // 19637
    println(part2(inputLines)) // 8811050362409
}

private fun part1(inputLines: List<String>): Int = parseDesertMap(inputLines).traverseFrom("AAA").takeWhile { it != "ZZZ" }.count()

private fun part2(inputLines: List<String>): Long {
    val graph = parseDesertMap(inputLines)
    val startNodes = graph.allNodes.filter { it.endsWith("A") }
    val paths = startNodes.map { start -> graph.describeLoopyPath(start, isEnd = { it.endsWith("Z") }) }
    paths.forEach {
        assert(it.endNodesIndices.size == 1) {
            "The data happens to be convenient, only one end node is found on the loop"
        }
        assert(it.endNodesIndices.single() == it.loopLength) {
            "The data happens to be convenient, the first end node is exactly as far from the start as the length of the loop"
        }
    }
    // LCM is only possible because of the happy "accidents" in the data asserted above
    return lcm(paths.map { it.endNodesIndices.single().toLong() })
}

private val nodeLineRegex = Regex("""(?<name>\w+) = \((?<left>\w+), (?<right>\w+)\)""")

private fun parseDesertMap(inputLines: List<String>): DesertMap {
    val directions = inputLines[0]
    val leftNodes = mutableMapOf<String, String>()
    val rightNodes = mutableMapOf<String, String>()
    inputLines.drop(2).forEach { line ->
        val match = nodeLineRegex.matchEntire(line) ?: error("Node line doesn't match regex: $line")
        val name = match.groups["name"]!!.value
        leftNodes[name] = match.groups["left"]!!.value
        rightNodes[name] = match.groups["right"]!!.value
    }
    return DesertMap(directions, leftNodes, rightNodes)
}

private data class DesertMap(
    val directions: String,
    private val leftEdges: Map<String, String>,
    private val rightEdges: Map<String, String>,
) {
    val allNodes: Set<String> get() = leftEdges.keys

    fun traverseFrom(start: String) = directions.asSequence()
        .repeatedIndefinitely()
        .scan(start) { node, dir -> neighborOf(node, dir) }

    fun neighborOf(source: String, direction: Char) = when (direction) {
        'L' -> leftEdges[source] ?: error("Unknown node $source")
        'R' -> rightEdges[source] ?: error("Unknown node $source")
        else -> error("Unknown direction $direction")
    }
}

private fun <T> Sequence<T>.repeatedIndefinitely(): Sequence<T> = sequence {
    while (true) {
        yieldAll(this@repeatedIndefinitely)
    }
}

// All paths must loop at some point, because the number of nodes and directions are finite.
// At most, a path visits all nodes at every position in the directions list, so the longest path is N*D jumps.
private fun DesertMap.describeLoopyPath(start: String, isEnd: (String) -> Boolean): LoopyPath {

    data class Position(val node: String, val dirIndex: Int)

    var directionIndex = 0
    val endNodesIndices = mutableListOf<Int>()
    val visited = mutableMapOf<Position, Int>()
    traverseFrom(start).forEachIndexed { absoluteIndex, node ->
        if (isEnd(node)) {
            endNodesIndices.add(absoluteIndex)
        }
        val pos = Position(node, directionIndex)
        directionIndex = (directionIndex + 1) % directions.length
        val visitedIndex = visited.put(pos, absoluteIndex)
        if (visitedIndex != null) {
            return LoopyPath(
                endNodesIndices = endNodesIndices,
                loopStartIndex = visitedIndex,
                loopLength = absoluteIndex - visitedIndex
            )
        }
    }
    error("Unreachable code, traversal should be infinite")
}

private data class LoopyPath(val endNodesIndices: List<Int>, val loopStartIndex: Int, val loopLength: Int)

private fun lcm(numbers: List<Long>): Long = numbers.reduce(::lcm)

private fun lcm(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}