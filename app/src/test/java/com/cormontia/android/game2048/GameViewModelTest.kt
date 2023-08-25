package com.cormontia.android.game2048

import org.junit.Test

class GameViewModelTest {
    //TODO?~ Move this inside the individual tests? Would be cleaner.
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
        val inputGameState = GameState(values,0)
        viewModel.setGameState(inputGameState)


        //viewModel.right()
        viewModel.right_using_FieldList()


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

    //TODO!~ Make more generic, and move to a utilities "class"...
    // (Might not even put it in a separate class, to make it widely available).
    private fun Map<Int,Int>.containsEntry(key: Int, value: Int) : Boolean {
        if (!this.containsKey(key))
            return false
        return (this[key]!! == value)
    }
}