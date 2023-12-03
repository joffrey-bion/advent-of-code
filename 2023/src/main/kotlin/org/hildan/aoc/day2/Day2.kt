package org.hildan.aoc.day2

import org.hildan.aoc.client.*

fun main() {
    val games = inputLines(year = 2023, day = 2).map { it.parseGame() }
    println(part1(games))
    println(part2(games))
}

private fun String.parseGame(): Game {
    val (game, setsStr) = split(":")
    return Game(
        id = game.removePrefix("Game ").toInt(),
        sets = setsStr.split(";").map { it.parseCubeSet() },
    )
}

private fun String.parseCubeSet(): CubeSet {
    val counts = split(",").map { it.trim().split(" ") }.associate { it[1] to it[0].toInt() }
    return CubeSet(
        red = counts["red"] ?: 0,
        green = counts["green"] ?: 0,
        blue = counts["blue"] ?: 0,
    )
}

private data class Game(
    val id: Int,
    val sets: List<CubeSet>,
)

private data class CubeSet(
    val red: Int,
    val green: Int,
    val blue: Int,
)

private fun part1(games: List<Game>): Int {
    val maxRed = 12
    val maxGreen = 13
    val maxBlue = 14
    return games.filter { it.sets.all { it.blue <= maxBlue && it.red <= maxRed && it.green <= maxGreen } }.sumOf { it.id }
}

private fun part2(games: List<Game>): Int = games.sumOf { it.minSet().power() }

private fun Game.minSet(): CubeSet = CubeSet(
    blue = sets.maxOf { it.blue },
    red = sets.maxOf { it.red },
    green = sets.maxOf { it.green },
)

private fun CubeSet.power() = red * green * blue