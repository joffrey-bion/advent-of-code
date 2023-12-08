package org.hildan.aoc.day7

import org.hildan.aoc.client.*

private val example = """32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483""".lines()

fun main() {
    val inputLines = inputLines(year = 2023, day = 7)
    println(part1(inputLines)) // 250957639
    println(part2(inputLines)) // 251515496
}

private fun part1(inputLines: List<String>): Long = inputLines.parseHands(useJokers = false)
    .sortedWith(handComparator(cardOrderPart1))
    .mapIndexed { i, hand -> hand.bid * (i + 1) }
    .sum()

private fun part2(inputLines: List<String>): Long = inputLines.parseHands(useJokers = true)
    .sortedWith(handComparator(cardOrderPart2))
    .mapIndexed { i, hand -> hand.bid * (i + 1) }
    .sum()

private fun List<String>.parseHands(useJokers: Boolean): List<Hand> = map { it.parseHand(useJokers) }

private fun String.parseHand(useJokers: Boolean): Hand {
    val (cards, bidStr) = split(" ")
    return Hand(
        cards = cards,
        bid = bidStr.toLong(),
        type = if (useJokers) computeHandTypeWithJokers(cards) else computeHandType(cards),
    )
}

private data class Hand(val cards: String, val bid: Long, val type: HandType)

private fun computeHandType(cards: String): HandType {
    val counts = cards.groupBy { it }.map { it.value.size }.sortedByDescending { it }
    return when (counts[0]) {
        5 -> HandType.FiveOfAKind
        4 -> HandType.FourOfAKind
        3 -> when (counts[1]) {
            2 -> HandType.FullHouse
            1 -> HandType.ThreeOfAKind
            else -> error("Cannot have more than 5 cards")
        }
        2 -> when (counts[1]) {
            2 -> HandType.TwoPair
            1 -> HandType.OnePair
            else -> error("Cannot have more than 5 cards")
        }
        1 -> HandType.HighCard
        else -> error("Cannot have 2 groups")
    }
}

private fun computeHandTypeWithJokers(cards: String): HandType {
    if (cards == "JJJJJ") {
        return HandType.FiveOfAKind
    }
    val cardFreqs = cards.groupBy { it }.mapValues { it.value.size }
    val mostFrequentCard = (cardFreqs - 'J').maxBy { (card, freq) -> freq * 15 + cardOrderPart2.indexOf(card) }.key
    return computeHandType(cards.replace('J', mostFrequentCard))
}

private enum class HandType {
    HighCard, OnePair, TwoPair, ThreeOfAKind, FullHouse, FourOfAKind, FiveOfAKind,
}

private const val cardOrderPart1 = "23456789TJQKA"
private const val cardOrderPart2 = "J23456789TQKA"

private fun handComparator(cardOrder: String): Comparator<Hand> {
    val cardsComparator = StringComparator(cardOrder)
    return compareBy<Hand> { it.type }.thenBy(cardsComparator) { it.cards }
}

class StringComparator(private val charOrder: String) : Comparator<String> {
    override fun compare(o1: String, o2: String): Int {
        for (i in o1.indices) {
            val charCompResult = compareValuesBy(o1[i], o2[i]) { charOrder.indexOf(it) }
            if (charCompResult != 0) {
                return charCompResult
            }
        }
        return 0
    }
}
