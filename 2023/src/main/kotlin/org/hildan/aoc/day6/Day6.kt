package org.hildan.aoc.day6

import org.hildan.aoc.client.*
import kotlin.math.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 6)
    println(part1(inputLines))
    println(part2(inputLines))
}

private fun part1(inputLines: List<String>): Long = inputLines.parseRaces().map { it.nWaysToBeatRecord() }.fold(1, Long::times)

private fun part2(inputLines: List<String>): Long = inputLines.parseSingleRace().nWaysToBeatRecord()

private fun List<String>.parseRaces(): List<Race> {
    val times = get(0).removePrefix("Time:").trim().split(Regex("""\s+""")).map { it.toLong() }
    val records = get(1).removePrefix("Distance:").trim().split(Regex("""\s+""")).map { it.toLong() }
    return times.zip(records) { t, r -> Race(duration = t, recordDistance = r) }
}

private fun List<String>.parseSingleRace(): Race {
    val time = get(0).removePrefix("Time:").trim().split(Regex("""\s+""")).joinToString("").toLong()
    val record = get(1).removePrefix("Distance:").trim().split(Regex("""\s+""")).joinToString("").toLong()
    return Race(duration = time, recordDistance = record)
}

private data class Race(val duration: Long, val recordDistance: Long)

private fun Race.nWaysToBeatRecord(): Long {
    val delta = duration * duration - 4 * recordDistance
    val min = ceil((duration - sqrt(delta.toDouble())) / 2).roundToLong()
    val max = floor((duration + sqrt(delta.toDouble())) / 2).roundToLong()
    return max - min + 1
}
