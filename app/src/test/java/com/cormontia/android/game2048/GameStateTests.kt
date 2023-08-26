package com.cormontia.android.game2048

import org.junit.Assert.*
import org.junit.Test

class GameStateTests {

    @Test
    fun testSerialize() {
        val state = mutableMapOf(
            Pair(Coor(1,3), 4),
            Pair(Coor(4,4), 8)
        )
        val score = 40
        val gameState = GameState(state, score)
        val jsonResult = gameState.serialize()
        assertEquals("40,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,8", jsonResult)
    }

    @Test
    fun testDeserialize() {
        val str = "40,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,8"
        val gameState = GameState(mutableMapOf(), 0)
        val actual = gameState.deserialize(str)
        assertEquals(40, actual.score)
        assertEquals(4, actual.state[Coor(1,3)])
        assertEquals(8, actual.state[Coor(4,4)])
        assertEquals(null, actual.state[Coor(2,3)])
    }

    @Test
    fun getFilteredRowAsFieldList() {
        // Input is this rectangular game board:
        //    | 2    | null |  4 |  4    | null |
        //    | null |  3   |  8 |  null | 2    |
        // We deliberately enter these NOT in order, to test if the sorting works!
        // Then we filter second row (1-based, so the row with index 2).
        // The method should result in a list: [3,8,2]
        val fields = mutableMapOf(
            Coor(1, 0) to 2,
            Coor(1, 3) to 4,
            Coor(2, 1) to 3,
            Coor(1, 2) to 4,
            Coor(2, 4) to 2,
            Coor(2, 2) to 8,
        )
        val gameState = GameState(fields, 0)

        val actual = gameState.getRowAsFilteredList(2)
        val expected = listOf(3,8,2)

        assertEquals(expected, actual)
    }
}