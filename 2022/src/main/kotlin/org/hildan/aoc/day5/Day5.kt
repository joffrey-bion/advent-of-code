package org.hildan.aoc.day5

import org.hildan.aoc.client.inputLines
import java.util.Stack

fun main() {
    val inputLines = inputLines(year = 2022, day = 5)
    println(part1(inputLines))
    println(part2(inputLines))
}

private fun part1(inputLines: List<String>): String {
    val (stacks, moves) = inputLines.parse()

    moves.forEach { move ->
        repeat(move.quantity) {
            val elt = stacks[move.sourceStack].pop()
            stacks[move.destStack].push(elt)
        }
    }
    return stacks.joinToString("") { it.peek().toString() }
}

private fun part2(inputLines: List<String>): String {
    val (stacks, moves) = inputLines.parse()

    moves.forEach { move ->
        val eltToMove = List(move.quantity) { stacks[move.sourceStack].pop() }
        eltToMove.reversed().forEach { stacks[move.destStack].push(it) }
    }
    return stacks.joinToString("") { it.peek().toString() }
}

private fun List<String>.parse(): Input {
    val stacksConfiguration = takeWhile { it.isNotEmpty() }
    val encodedMoves = drop(stacksConfiguration.size).dropWhile { it.isEmpty() }

    val stacks = parseStacks(stacksConfiguration)
    val moves = encodedMoves.map { Move.parse(it) }
    return Input(stacks, moves)
}

private fun parseStacks(stacksConfiguration: List<String>): List<Stack<Char>> {
    val stackIndicesRow = stacksConfiguration.last()
    val stackDataRows = stacksConfiguration.dropLast(1)

    val stackIndices = stackIndicesRow.trim().split(Regex("\\s+"))
    val stackIndicesToPositionInRow = stackIndices.associate { (it.toInt() - 1) to stackIndicesRow.indexOf(it) }

    val stacks = List(stackIndices.size) { Stack<Char>() }
    stackDataRows.reversed().forEach { row ->
        stacks.forEachIndexed { index, stack ->
            val pos = stackIndicesToPositionInRow[index] ?: error("Position not found for stack $index")
            val elt = row[pos]
            if (!elt.isWhitespace()) {
                stack.push(elt)
            }
        }
    }
    return stacks
}

private data class Input(val stacks: List<Stack<Char>>, val moves: List<Move>)

private data class Move(val quantity: Int, val sourceStack: Int, val destStack: Int) {

    companion object {
        private val moveRegex = Regex("""move (\d+) from (\d+) to (\d+)""")

        fun parse(moveLine: String): Move {
            val match = moveRegex.matchEntire(moveLine) ?: error("Invalid move line '$moveLine'")
            return Move(
                quantity = match.groupValues[1].toInt(),
                sourceStack = match.groupValues[2].toInt() - 1, // 0-based index
                destStack = match.groupValues[3].toInt() - 1, // 0-based index
            )
        }
    }
}
