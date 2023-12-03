package org.hildan.aoc.day4

import org.hildan.aoc.client.inputLines

fun main() {
    val inputLines = inputLines(year = 2022, day = 4)
    println(inputLines.count { it.hasRedundantAssignment() })
    println(inputLines.count { it.hasOverlappingAssignments() })
}

private fun String.hasRedundantAssignment(): Boolean {
    val (range1, range2) = parseAssignments()
    return range1.fullyContains(range2) || range2.fullyContains(range1)
}

private fun String.hasOverlappingAssignments(): Boolean {
    val (range1, range2) = parseAssignments()
    return range1.overlapsWith(range2)
}

private fun String.parseAssignments(): List<IntRange> = split(",").map { it.parseRange() }

private fun String.parseRange(): IntRange {
    val (start, end) = split("-").map { it.toInt() }
    return start..end
}

private fun IntRange.fullyContains(other: IntRange): Boolean = first <= other.first && other.last <= last

private fun IntRange.overlapsWith(other: IntRange): Boolean = !mutuallyExclusiveWith(other)

private fun IntRange.mutuallyExclusiveWith(other: IntRange): Boolean = last < other.first || other.last < first
