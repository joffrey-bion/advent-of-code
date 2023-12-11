package org.hildan.aoc.day12

import org.hildan.aoc.client.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 12)
    println(part1(inputLines)) // 8075
    println(part2(inputLines)) // 4232520187524
}

private fun part1(inputLines: List<String>): Long = inputLines.map { it.split(" ") }.sumOf { (pattern, groups) ->
    nArr(pattern, groups.split(",").map { it.toInt() })
}

private fun part2(inputLines: List<String>): Long = inputLines.map { it.split(" ") }.sumOf { (pattern, groups) ->
    val effectivePattern = List(5) { pattern }.joinToString("?")
    val effectiveGroups = List(5) { groups.split(",").map { it.toInt() } }.flatten()
    nArr(effectivePattern, effectiveGroups)
}

private val cache = mutableMapOf<String, MutableMap<List<Int>, Long>>()

private fun nArr(pattern: String, groups: List<Int>): Long {
    cache[pattern]?.get(groups)?.let { return it }
    if (groups.isEmpty()) {
        // We have no groups to find, so any # makes it impossible.
        // If there are no '#', we must just set all '?' to '.', which is one way.
        return if (pattern.any { it == '#' }) 0 else 1
    }
    val trimmed = pattern.dropWhile { it == '.' }
    val gWidth = groups[0]
    if (trimmed.length < gWidth) {
        return 0 // the group cannot fit
    }
    val canFitAtStart = trimmed.take(gWidth).all { it == '#' || it == '?' }
    if (trimmed.length == gWidth) {
        return if (groups.size == 1 && canFitAtStart) 1 else 0
    }
    val nextCanBeSep = trimmed[gWidth] == '.' || trimmed[gWidth] == '?'
    val remainingPattern = trimmed.substring(gWidth + 1)
    val nWaysWithGroupAtStart = if (canFitAtStart && nextCanBeSep) nArr(remainingPattern, groups.drop(1)) else 0
    val nWaysWithoutGroupAtStart = if (trimmed[0] != '#') nArr(trimmed.substring(1), groups) else 0
    return (nWaysWithGroupAtStart + nWaysWithoutGroupAtStart).also {
        cache.getOrPut(pattern) { mutableMapOf() }[groups] = it
    }
}
