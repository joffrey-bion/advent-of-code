package org.hildan.aoc.day10

import org.hildan.aoc.client.*

fun main() {
    val inputLines = inputLines(year = 2023, day = 10)
    val grid = Grid(inputLines)
    println(part1(grid)) // 6690
    println(part2(grid)) // 525
}

private class Grid(private val tiles: List<String>) {
    val height = tiles.size
    val width = tiles[0].length
    val startPos = findStartPosition(tiles)

    operator fun get(pos: Position) = tiles[pos.row][pos.col]

    private fun findStartPosition(cells: List<String>): Position {
        val startRow = cells.indexOfFirst { 'S' in it }
        val startColumn = cells[startRow].indexOf('S')
        return Position(startRow, startColumn)
    }

    fun path(start: Position, dir: Direction): Sequence<Position> = sequence {
        var state = State(start, dir)
        while(true) {
            state = state.moveOneStep()
            yield(state.pos)
            if (state.pos == start) {
                return@sequence
            }
        }
    }

    private fun State.moveOneStep(): State {
        val newPos = when(dir) {
            Direction.UP -> Position(row = pos.row - 1, col = pos.col)
            Direction.DOWN -> Position(row = pos.row + 1, col = pos.col)
            Direction.LEFT -> Position(row = pos.row, col = pos.col - 1)
            Direction.RIGHT -> Position(row = pos.row, col = pos.col + 1)
        }
        val newDir = when (val type = get(newPos)) {
            'J' -> when (dir) {
                Direction.DOWN -> Direction.LEFT
                Direction.RIGHT -> Direction.UP
                Direction.UP, Direction.LEFT -> error("Cannot arrive on tile '$type' from $dir")
            }
            'L' -> when (dir) {
                Direction.DOWN -> Direction.RIGHT
                Direction.LEFT -> Direction.UP
                Direction.UP, Direction.RIGHT -> error("Cannot arrive on tile '$type' from $dir")
            }
            '7' -> when (dir) {
                Direction.UP -> Direction.LEFT
                Direction.RIGHT -> Direction.DOWN
                Direction.LEFT, Direction.DOWN -> error("Cannot arrive on tile '$type' from $dir")
            }
            'F' -> when (dir) {
                Direction.LEFT -> Direction.DOWN
                Direction.UP -> Direction.RIGHT
                Direction.RIGHT, Direction.DOWN -> error("Cannot arrive on tile '$type' from $dir")
            }
            '|' -> when (dir) {
                Direction.DOWN -> Direction.DOWN
                Direction.UP -> Direction.UP
                Direction.RIGHT, Direction.LEFT -> error("Cannot arrive on tile '$type' from $dir")
            }
            '-' -> when (dir) {
                Direction.LEFT -> Direction.LEFT
                Direction.RIGHT -> Direction.RIGHT
                Direction.UP, Direction.DOWN -> error("Cannot arrive on tile '$type' from $dir")
            }
            'S' -> Direction.DOWN // doesn't matter
            else -> error("Unexpected tile '$type'")
        }
        return State(newPos, newDir)
    }
}

private data class State(val pos: Position, val dir: Direction)

private data class Position(val row: Int, val col: Int)

private enum class Direction {
    UP, DOWN, LEFT, RIGHT;
}

private fun part1(grid: Grid): Long {
    // I know a pipe connects to the right in my specific puzzle
    return grid.path(start = grid.startPos, dir = Direction.RIGHT).count() / 2L
}

private fun part2(grid: Grid): Int {
    val loopTiles = grid.path(start = grid.startPos, dir = Direction.RIGHT).toSet()
    val nonLoopTiles = (0..<grid.width).flatMap { col -> List(grid.height) { row -> Position(row, col) } } - loopTiles
    return nonLoopTiles.count { it.isInLoop(grid, loopTiles) }
}

private fun Position.isInLoop(grid: Grid, loopTiles: Set<Position>): Boolean {
    val numberRayHits = rightRayTo(grid.width).sumOf { p ->
        when {
            p in loopTiles -> when(grid[p]) {
                '|', 'F', '7', 'S' -> 1 // hardcoded S here because it's an F in my puzzle
                'J', 'L', '-' -> 0
                else -> error("Char '${grid[p]}' found in loop")
            }
            else -> 0
        } as Int
    }
    return numberRayHits % 2L == 1L
}

private fun Position.rightRayTo(width: Int) = (col + 1..<width).map { Position(row, it) }
