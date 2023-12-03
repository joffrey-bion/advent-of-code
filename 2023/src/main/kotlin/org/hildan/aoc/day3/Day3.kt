package org.hildan.aoc.day3

import org.hildan.aoc.client.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 3)
    println(part1(inputLines))
    println(part2(inputLines)) // 30687788 too low
}

private fun part1(inputLines: List<String>): Int {
    val hitsByRow = positionsAdjacentToSymbols(inputLines)
        .groupBy(keySelector = { it.row }, valueTransform = { it.col })
        .mapValues { (_, v) -> v.toSet() }
    return inputLines.flatMapIndexed { row, line ->
        val hits = hitsByRow[row] ?: emptySet()
        findPartNumbers(line, hits)
    }.sum()
}


private val symbolRegex = Regex("""([^.\d])""")

private data class Pos(val row: Int, val col: Int)

private fun positionsAdjacentToSymbols(inputLines: List<String>): Set<Pos> =
    inputLines.flatMapIndexedTo(HashSet()) { index, line ->
        findSymbols(index, line).flatMap { it.neighbors() }
    }

private fun findSymbols(row: Int, line: String): Sequence<Pos> =
    symbolRegex.findAll(line).map { Pos(row = row, col = it.range.first) }

private fun Pos.neighbors(): Set<Pos> = setOf(
    Pos(row - 1, col - 1),
    Pos(row, col - 1),
    Pos(row + 1, col - 1),
    Pos(row - 1, col),
    Pos(row + 1, col),
    Pos(row - 1, col + 1),
    Pos(row, col + 1),
    Pos(row + 1, col + 1),
)

private val numRegex = Regex("""(\d+)""")

private fun findPartNumbers(line: String, hitColumns: Set<Int>): Sequence<Int> =
    numRegex.findAll(line)
        .filter { (it.range intersect hitColumns).isNotEmpty() }
        .map { it.groupValues[1].toInt() }

private fun part2(inputLines: List<String>): Int {
    val numByPos = inputLines.flatMapIndexed { row, line -> numByPos(row, line) }.reduce { m1, m2 -> m1 + m2 }
    val gearPositions = inputLines.flatMapIndexed { row, line -> gearPositions(row, line) }
    return gearPositions.sumOf { gearPos ->
        val adjNums = gearPos.neighbors().mapNotNull { numByPos[it] }.distinct()
        if (adjNums.size == 2) {
            adjNums[0] * adjNums[1]
        } else {
            0
        }
    }
}

private fun numByPos(row: Int, line: String) = numRegex.findAll(line).map { numByPos(row, it) }

private fun numByPos(row: Int, numberMatch: MatchResult): Map<Pos, Int> {
    val num = numberMatch.groupValues[1].toInt()
    return numberMatch.range.map { Pos(row, it) }.associateWith { num }
}

private fun gearPositions(row: Int, line: String) = line.mapIndexedNotNull { col, c -> if (c == '*') Pos(row, col) else null }
