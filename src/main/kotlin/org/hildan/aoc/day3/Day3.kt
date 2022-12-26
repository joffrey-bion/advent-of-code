package org.hildan.aoc.day3

import org.hildan.aoc.client.inputLines

fun main() {
    val inputLines = inputLines(day = 3)
    println(inputLines.sumOf { findDuplicateType(it).priority() })
    println(inputLines.chunked(3).sumOf { it.singleCommonChar().priority() })
}

private fun findDuplicateType(contents: String): Char = contents.splitInHalf().singleCommonChar()

private fun String.splitInHalf() = chunked(length / 2)

private fun List<String>.singleCommonChar(): Char = map { it.toSet() }.intersectAll().single()

private fun <T> Iterable<Set<T>>.intersectAll() = reduce { acc, set -> acc intersect set }

// Lowercase item types a through z have priorities 1 through 26.
// Uppercase item types A through Z have priorities 27 through 52.
private fun Char.priority(): Int = if (isLowerCase()) this - 'a' + 1 else this - 'A' + 27
