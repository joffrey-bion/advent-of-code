package org.hildan.aoc.day1

import org.hildan.aoc.client.*

fun main() {
    val lines = inputLines(year = 2023, day = 1)
    println(lines.sumOf { it.calibrationNumber() })
    println(lines.sumOf { it.calibrationNumber2() })
}

private fun String.calibrationNumber(): Int {
    val first = first { it.isDigit() }
    val last = last { it.isDigit() }
    return "$first$last".toInt()
}

private val digits = List(10) { "$it" to it }.toMap() + mapOf(
    "zero" to 0,
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)

private fun String.calibrationNumber2(): Int {
    val first = findAnyOf(digits.keys)?.second?.let { digits.getValue(it) }!!
    val last = findLastAnyOf(digits.keys)?.second?.let { digits.getValue(it) }!!
    return first * 10 + last
}
