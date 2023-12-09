package org.hildan.aoc.day9

import org.hildan.aoc.client.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 9)
    val series = inputLines.map { line -> line.split(" ").map { it.toLong() } }
    println(part1(series)) // 1974913025
    println(part2(series)) // 884
}

private fun part1(series: List<List<Long>>): Long = series.sumOf { it.predictNext() }
private fun part2(series: List<List<Long>>): Long = series.sumOf { it.predictPrev() }

private fun List<Long>.predictNext(): Long = if (all { it == 0L }) 0L else last() + differences().predictNext()
private fun List<Long>.predictPrev(): Long = if (all { it == 0L }) 0L else first() - differences().predictPrev()

private fun List<Long>.differences(): List<Long> = zipWithNext { a, b -> b - a }
