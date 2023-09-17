package com.cormontia.android.game2048

import org.junit.Test

class GameViewModelTest {

    private val viewModel = GameViewModel()

    @Test
    fun right_shift_collapses_doubles_and_gives_score() {
        // Test that (2,2,_,_) and (2,4,4,8) gives (_,_,_,4) and (_,2,8,8), and scores 12 points.
        val values = mutableMapOf(
            Coor(1, 1) to 2,
            Coor(1, 2) to 2,
            Coor(2, 1) to 2,
            Coor(2, 2) to 4,
            Coor(2, 3) to 4,
            Coor(2, 4) to 8,
        )
        val inputGameState = GameState(4, 4, values,0)
        viewModel.setGameState(inputGameState)

        viewModel.right()

        val outputGameState = viewModel.getGameState()

        val actualRow1 = outputGameState.getRow(1)
        assert(actualRow1.size == 1)
        assert(actualRow1.containsEntry(4, 4))

        val actualRow2 = outputGameState.getRow(2)
        assert(actualRow2.size == 3)
        assert(actualRow2.containsEntry(2, 2))
        assert(actualRow2.containsEntry(3, 8))
        assert(actualRow2.containsEntry(4, 8))

        assert(outputGameState.score == 12)
    }

    @Test
    fun a_change_is_noted_for_consecutive_values_even_if_there_are_no_collapses() {
        val values = mutableMapOf(
            Coor(1, 1) to 4,
            Coor(1, 2) to 8,
            Coor(1, 3) to 2,
            // An empty fourth position is implied.
        )
        val inputGameState = GameState(4, 4, values,0)
        viewModel.setGameState(inputGameState)

        val res = viewModel.right()

        assert(res.changeOccurred)
    }

    @Test
    fun when_positions_remain_unchanged_then_we_observe_that_there_is_no_change() {
        val values = mutableMapOf(
            Coor(1, 1) to 4,
            Coor(1, 2) to 8,
            Coor(1, 3) to 2,
            Coor(1, 4) to 16,
        )
        val inputGameState = GameState(4, 4, values, 0)
        viewModel.setGameState(inputGameState)

        val res = viewModel.right()

        assert(!res.changeOccurred)
    }

    @Test
    fun a_change_is_noted_for_rows_containing_empty_values_even_if_there_are_no_collapses() {
        val values = mutableMapOf(
            Coor(1, 1) to 4,
            Coor(1, 2) to 8,
            // Note that column 3 remains empty this time.
            Coor(1, 4) to 2,
        )
        val inputGameState = GameState(4, 4, values,0)
        viewModel.setGameState(inputGameState)

        val res = viewModel.right()

        assert(res.changeOccurred)
    }


    @Test
    fun left_shift_collapses_doubles_and_gives_score() {
        // Test that (_,_,2,2) and (8,4,4,2) gives (4,_,_,_) and (8,8,2,_), and scores 12 points.
        val values = mutableMapOf(
            Coor(1, 3) to 2,
            Coor(1, 4) to 2,
            Coor(2, 1) to 8,
            Coor(2, 2) to 4,
            Coor(2, 3) to 4,
            Coor(2, 4) to 2,
        )
        val inputGameState = GameState(4, 4, values,0)
        viewModel.setGameState(inputGameState)

        viewModel.left()

        val outputGameState = viewModel.getGameState()

        val actualRow1 = outputGameState.getRow(1)
        assert(actualRow1.size == 1)
        assert(actualRow1.containsEntry(1, 4))

        val actualRow2 = outputGameState.getRow(2)
        assert(actualRow2.size == 3)
        assert(actualRow2.containsEntry(1, 8))
        assert(actualRow2.containsEntry(2, 8))
        assert(actualRow2.containsEntry(3, 2))

        assert(outputGameState.score == 12)
    }

    @Test
    fun when_shifting_right_the_rightmost_duplicates_get_collapsed() {
        // (4,4,4,8) should become (_,4,8,8). Not (_,8,4,8).
        val values = mutableMapOf(
            Coor(1, 1) to 4,
            Coor(1, 2) to 4,
            Coor(1, 3) to 4,
            Coor(1, 4) to 8,
        )
        val inputGameState = GameState(4, 4, values, 0)
        viewModel.setGameState(inputGameState)

        viewModel.right()

        val row = viewModel.getGameState().getRow(1)
        assert(row.size == 3)
        assert(row.containsEntry(2, 4))
        assert(row.containsEntry(3, 8))
        assert(row.containsEntry(4, 8))
    }

    @Test
    fun when_shifting_left_the_leftmost_duplicates_get_collapsed() {
        // (8,4,4,4) should become (8,8,4,_). Not (8,4,8,_).
        val values = mutableMapOf(
            Coor(1, 1) to 8,
            Coor(1, 2) to 4,
            Coor(1, 3) to 4,
            Coor(1, 4) to 4,
        )
        val inputGameState = GameState(4, 4, values, 0)
        viewModel.setGameState(inputGameState)

        viewModel.left()

        val row = viewModel.getGameState().getRow(1)
        assert(row.size == 3)
        assert(row.containsEntry(1, 8))
        assert(row.containsEntry(2, 8))
        assert(row.containsEntry(3, 4))
    }

    @Test
    fun testEmptySeriesToString() {
        val series = mapOf<Int,Int>()
        val text = viewModel.seriesToString(series)

        assert(text == "")
    }

    @Test
    fun testSeriesToString() {
        val series = mapOf(1 to 2, 2 to 4, 3 to 0)
        val text = viewModel.seriesToString(series)

        assert(text == "(1->2), (2->4), (3->0)")
    }

    //TODO!~ Make more generic, and move to a utilities "class"...
    // (Might not even put it in a separate class, to make it widely available).
    private fun Map<Int,Int>.containsEntry(key: Int, value: Int) : Boolean {
        if (!this.containsKey(key))
            return false
        return (this[key]!! == value)
    }
}