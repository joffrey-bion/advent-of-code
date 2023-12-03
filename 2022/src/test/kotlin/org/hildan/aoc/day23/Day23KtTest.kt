package org.hildan.aoc.day23

import junit.framework.TestCase.assertEquals
import org.hildan.aoc.day23.*
import org.hildan.aoc.day23.Direction
import org.hildan.aoc.day23.Position
import org.hildan.aoc.day23.col
import org.hildan.aoc.day23.row
import org.junit.Test

class Day23KtTest {

    @Test
    fun test() {
        val p = Position(42, 12)
        assertEquals(42, p.row)
        assertEquals(12, p.col)
    }

    @Test
    fun testNegative() {
        val p2 = Position(-42, -12)
        assertEquals(-42, p2.row)
        assertEquals(-12, p2.col)
    }

    @Test
    fun testMove() {
        val p2 = Position(1, 2)
        assertEquals(Position(2, 2), p2.moved(Direction.SOUTH))
        assertEquals(Position(2, 3), p2.moved(Direction.SOUTH, Direction.EAST))
        assertEquals(p2, p2.moved(Direction.SOUTH, Direction.NORTH))
    }
}
