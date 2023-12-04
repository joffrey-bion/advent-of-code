package org.hildan.aoc.day4

import org.hildan.aoc.client.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 4)
    println(part1(inputLines))
    println(part2(inputLines))
}

private fun part1(inputLines: List<String>): Int = inputLines.sumOf { it.computePoints() }

private fun String.computePoints(): Int {
    val numberOfWins = nMatchingNums()
    return if (numberOfWins > 0) 1 shl (numberOfWins - 1) else 0
}

private fun part2(inputLines: List<String>): Int {
    val nMatchesPerCard = inputLines.associate { it.parseId() to it.nMatchingNums() }
    val nCardsWonPerCard = mutableMapOf<Int, Int>()
    inputLines.reversed().forEach { line ->
        val id = line.parseId()
        val m = nMatchesPerCard.getValue(id)
        val nCardsWon = m + (1..m).sumOf { nCardsWonPerCard.getValue(id + it) }
        nCardsWonPerCard[id] = nCardsWon
    }
    return nCardsWonPerCard.values.sum() + nCardsWonPerCard.size
}

private fun String.parseId(): Int = removePrefix("Card ").substringBefore(':').trim().toInt()

private fun String.nMatchingNums(): Int {
    val (winningNums, myNums) = substringAfter(':').split("|").map { it.parseNums() }
    return (winningNums intersect myNums).size
}

private fun String.parseNums() = trim().split(Regex("""\s+""")).mapTo(HashSet()) { it.toInt() }
