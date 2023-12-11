package org.hildan.aoc.day11

import org.hildan.aoc.client.*
import java.util.TreeSet
import kotlin.math.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 11)
    println(part1(inputLines)) // 9918828
    println(part2(inputLines)) // 1016799176
}

private fun part1(inputLines: List<String>): Long =
    distanceBetweenPairs(expandedGalaxyPositions(inputLines, expansionFactor = 2))

private fun part2(inputLines: List<String>): Long =
    distanceBetweenPairs(expandedGalaxyPositions(inputLines, expansionFactor = 1_000_000))

private data class Position(val row: Int, val col: Int)

private fun expandedGalaxyPositions(inputLines: List<String>, expansionFactor: Int): List<Position> {
    val emptyRows = inputLines.indices.filterTo(TreeSet()) { row -> inputLines[row].all { it == '.' } }
    val emptyCols = inputLines[0].indices.filterTo(TreeSet()) { col -> inputLines.all { it[col] == '.' } }

    val galaxies = inputLines.flatMapIndexed { row, line ->
        line.mapIndexedNotNull { col, c -> if (c == '#') Position(row, col) else null }
    }
    return galaxies.map { (row, col) ->
        // we count another time every empty row or column to effectively "double" them,
        // but only the ones before our own coordinates actually affect the resulting coordinates
        val newRow = row + (emptyRows.subSet(0, row).size * (expansionFactor - 1))
        val newCol = col + (emptyCols.subSet(0, col).size * (expansionFactor - 1))
        Position(newRow, newCol)
    }
}

private fun distanceBetweenPairs(expandedGalaxies: List<Position>): Long {
    var totalDistance = 0L
    for (i in expandedGalaxies.indices) {
        for (j in i..<expandedGalaxies.size) {
            totalDistance += expandedGalaxies[i].manhattanDistanceTo(expandedGalaxies[j])
        }
    }
    return totalDistance
}

private fun Position.manhattanDistanceTo(other: Position): Int = abs(other.row - row) + abs(other.col - col)
