package org.hildan.aoc.day5

import org.hildan.aoc.client.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 5)
    val input = inputLines.parseInput()
    println(part1(input))
    println(part2(input))
}

private fun part1(input: Input): Long = input.seeds.minOf { input.findLocation(it) }

private fun part2(input: Input): Long {
    val seedRanges = input.seeds.chunked(2).map { (start, length) -> start..<start + length }
    return seedRanges.minOf { input.findMinLocation(it) }
}

private fun List<String>.parseInput(): Input {
    val seeds = get(0).removePrefix("seeds: ").split(" ").map { it.toLong() }
    val mappings = drop(2).parseMappings()
    return Input(seeds, mappings)
}

private fun List<String>.parseMappings(): MutableList<Mapping> {
    val mappings = mutableListOf<Mapping>()
    val currentMappingRanges = mutableListOf<RangeMapping>()
    forEach {
        if (it.isEmpty()) {
            mappings.add(Mapping(currentMappingRanges.toList()))
            currentMappingRanges.clear()
        } else if (it[0].isDigit()) {
            currentMappingRanges.add(it.parseRangeMapping())
        }
    }
    mappings.add(Mapping(currentMappingRanges.toList()))
    currentMappingRanges.clear()
    return mappings
}

private fun String.parseRangeMapping(): RangeMapping {
    val (destStart, sourceStart, length) = split(" ").map { it.toLong() }
    return RangeMapping(destStart, sourceStart, length)
}

private class Input(val seeds: List<Long>, val mappings: List<Mapping>) {
    fun findLocation(seed: Long): Long = mappings.fold(seed) { v, m -> m[v] }
    fun findMinLocation(seedRange: LongRange): Long =
        mappings.fold(listOf(seedRange)) { v, m -> m[v] }.minOf { it.first }
}

private class Mapping(ranges: List<RangeMapping>) {
    private val sortedRanges = ranges.sortedBy { it.sourceStart }
    private val contiguousRanges = sortedRanges.makeContiguous()

    operator fun get(source: Long): Long = sortedRanges.firstNotNullOfOrNull { it[source] } ?: source

    operator fun get(sources: List<LongRange>): List<LongRange> = sources.flatMap { get(it) }

    operator fun get(source: LongRange): List<LongRange> {
        val destRanges = contiguousRanges.mapNotNull { it[source] }
        val mappedSourceEndExclusive = contiguousRanges.last().sourceEndExclusive
        return if (source.last >= mappedSourceEndExclusive) {
            destRanges + listOf(mappedSourceEndExclusive..source.last)
        } else {
            destRanges
        }
    }
}

private fun List<RangeMapping>.makeContiguous(): List<RangeMapping> {
    val fillers = zipWithNext { r1, r2 -> r1.rangeMappingTo(r2) }.filterNotNull()
    return (this + fillers).sortedBy { it.sourceStart }
}

private fun RangeMapping.rangeMappingTo(next: RangeMapping): RangeMapping? {
    val gap = next.sourceStart - sourceEndExclusive
    if (gap <= 0) {
        return null
    }
    return RangeMapping(destStart = sourceEndExclusive, sourceStart = sourceEndExclusive, length = gap)
}

private data class RangeMapping(
    val destStart: Long,
    val sourceStart: Long,
    val length: Long,
) {
    val sourceEndExclusive = sourceStart + length
    private val sourceRange = sourceStart..<sourceEndExclusive

    operator fun get(source: Long): Long? =
        if (source in sourceRange) destStart - sourceStart + source else null

    operator fun get(source: LongRange): LongRange? {
        val intersectionStart = maxOf(source.first, sourceRange.first)
        val intersectionEnd = minOf(source.last, sourceRange.last)
        if (intersectionStart > intersectionEnd) {
            return null
        }
        return get(intersectionStart)!!..get(intersectionEnd)!!
    }
}
