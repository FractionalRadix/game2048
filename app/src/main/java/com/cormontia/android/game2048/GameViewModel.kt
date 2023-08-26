package com.cormontia.android.game2048

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.HashMap

//TODO?~ We need TWO game-states here.
// The present game state, and the game state that is being built from the player's move.
// That saves a lot of deep-copying.

class GameViewModel : ViewModel() {
    //TODO?~ Parameterize nr of rows and nr of columns?
    private var currentGameState = GameState(4, 4, HashMap(),0)
    private val random = Random()

    private var history = mutableListOf<GameState>()
    private var historyIndex = 0

    /**
     * Check if the game has only just started, or if `init` is called due to an orientation change.
     */
    private var startOfGame = true

    companion object StaticMethods {

        /**
         * Determine if two mappings from Int to Int, contain precisely the same mappings.
         * @param map1 A map from Int to Int.
         * @param map2 A map from Int to Int.
         * @return <code>true<code> if and only if map1 contains exactly the same mappings as map2.
         */
        fun equal(map1: Map<Int,Int>, map2:Map<Int,Int>): Boolean {
            val keySet1 = map1.keys
            val keySet2 = map2.keys

            if (keySet1.size != keySet2.size) {
                return false
            }

            if (!keySet1.containsAll(keySet2)) {
                return false
            }

            val mappingsDiffer = keySet1.any { map1[it] != map2[it] }
            if (mappingsDiffer) {
                return false
            }

            return true
        }
    }

    fun init() {
        /* Initialize the game, but only if this is really a new game.
         * If the user has only changed the orientation of the device, do nothing.
         */
        if (startOfGame) {
            placeNewValue()
            startOfGame = false

            historyIndex = 0
            history.clear()
        }
    }

    fun startNewGame() {
        currentGameState.state.clear()
        //TODO?~ Parameterize nr of rows and nr of columns?
        currentGameState = GameState(4, 4, HashMap(), 0)
        placeNewValue()

        historyIndex = 0
        history.clear()
    }

    //TODO?~ Use an Observable...?
    fun getGameState(): GameState {
        return currentGameState
    }

    fun setGameState(state: GameState) {
        currentGameState = state
        //TODO?+ Notify observers of a changed game state...?
    }

    /**
     * Find a random empty spot on the game board.
     * Put a 2 or a 4 in it.
     * If there is no empty spot, then do nothing.
     */
    fun placeNewValue() {
        val emptySlots = findEmptyPositions()

        val nrOfEmptySlots = emptySlots.size
        if (nrOfEmptySlots > 0) {
            history.add(currentGameState)
            val randomIndex = random.nextInt(nrOfEmptySlots)
            val selectedField = emptySlots[randomIndex]
            val value = if (random.nextInt(10) == 1) 4 else 2
            currentGameState.state[selectedField] = value
        }
    }

    /**
     * Find all empty positions on the game board.
     * @return A list of all positions on the game board, that do not contain a value.
     */
    private fun findEmptyPositions(): List<Coor> {
        val emptySlots = mutableListOf<Coor>()
        for (rowIdx in 1..getGameState().nrOfRows) {
            for (colIdx in 1..getGameState().nrOfColumns) {
                if (!currentGameState.state.containsKey(Coor(rowIdx, colIdx))) {
                    emptySlots.add(Coor(rowIdx, colIdx))
                }
            }
        }
        return emptySlots.toList()
    }

    /**
     * Determine if there are still moves that the user can make.
     * @return <code>true</code> if and only if the user can still make a legal move.
     */
    fun movesAvailable(): Boolean {
        // 1. If there is at least 1 open field, then the user can make a move.
        if (!findEmptyPositions().isEmpty())
            return true

        // 2. If there are at least 2 fields with the same value adjacent to each other, then the user can make a move.
        for (rowIdx in 1..getGameState().nrOfRows) {
            for (colIdx in 1..getGameState().nrOfColumns - 1) {
                if (currentGameState.state[Coor(rowIdx, colIdx)] == currentGameState.state[Coor(rowIdx, colIdx + 1)])
                    return true
            }
        }

        for (colIdx in 1..getGameState().nrOfColumns) {
            for (rowIdx in 1..getGameState().nrOfRows - 1) {
                if (currentGameState.state[Coor(rowIdx, colIdx)] == currentGameState.state[Coor(rowIdx + 1, colIdx)])
                    return true
            }
        }

        // 3. If neither of these conditions hold, then the player has no more moves available.
        return false
    }

    //TODO?~ Move "right()", "left()", "up()", and "down()" to GameState?
    /**
     * Adjust the game board if the player moves values to the right.
     * Using the new "FieldList" interface.
     * @return <code>true</code> if and only if this move caused a change in the game board.
     */
    fun right(): MoveResult {

        var changeOccurred = false
        val cachedGameState = currentGameState.deepCopy()
        var highestNewValue = 0

        for (rowIdx in 1 .. getGameState().nrOfRows) {

            // Determine the new row. Also maintain the score.
            val row = currentGameState.getRowAsReverseFilteredList(rowIdx)
            val shiftAndCollapseResult = FieldList.shiftCollapseAndCalculateScore(row)
            val shiftedRow = shiftAndCollapseResult.first
            currentGameState.score += shiftAndCollapseResult.second

            // Remove the old row.
            currentGameState.state.keys.removeIf { it.row == rowIdx }

            // Insert the transformed row.
            // Note that the row is inserted in reverse order, since it was originally retrieved in reverse order.
            val startPos = getGameState().nrOfColumns - shiftedRow.size
            for (colIdx in startPos until getGameState().nrOfColumns) {
                val value = shiftedRow[ getGameState().nrOfColumns - colIdx  - 1]
                val coor = Coor(rowIdx, colIdx + 1)
                currentGameState.state[coor] = value
            }

            //TODO?~ Maybe this could be done at a later stage.
            // Check if the new row is different from the original row.
            val originalRow = cachedGameState.getRow(rowIdx)
            val newRow = currentGameState.getRow(rowIdx)
            changeOccurred = changeOccurred || !equal(originalRow, newRow)

            highestNewValue = determineHighestNewValue(originalRow, newRow, highestNewValue)
        }

        if (changeOccurred) {
            updateHistory(cachedGameState)
        }

        return MoveResult(changeOccurred, highestNewValue)
    }

    //TODO!~ Get the parts in move[Right|Left|Up|Down] that differ, and use function parameters for these.
    //  Ultimately we want a generic "move(d: Direction)" method, where Direction is an enum containing Left, Right, Up, and Down.

    /**
     * Adjust the game board if the player moves values to the left.
     * Using the new "FieldList" interface.
     * @return <code>true</code> if and only if this move caused a change in the game board.
     */
    fun left(): MoveResult {

        var changeOccurred = false
        val cachedGameState = currentGameState.deepCopy()
        var highestNewValue = 0

        for (rowIdx in 1 .. getGameState().nrOfRows) {

            // Determine the new row. Note if it is different from the old one, and maintain the score.
            val row = currentGameState.getRowAsFilteredList(rowIdx)
            val shiftAndCollapseResult = FieldList.shiftCollapseAndCalculateScore(row)
            val shiftedRow = shiftAndCollapseResult.first
            currentGameState.score += shiftAndCollapseResult.second

            // Remove the old row.
            currentGameState.state.keys.removeIf { it.row == rowIdx }

            // Insert the transformed row.
            for (colIdx in shiftedRow.indices) {
                val value = shiftedRow[colIdx]
                val coor = Coor(rowIdx, colIdx + 1)
                currentGameState.state[coor] = value
            }

            //TODO?~ Maybe this could be done at a later stage.
            // Check if the new row is different from the original row.
            val originalRow = cachedGameState.getRow(rowIdx)
            val newRow = currentGameState.getRow(rowIdx)
            changeOccurred = changeOccurred || !equal(originalRow, newRow)

            highestNewValue = determineHighestNewValue(originalRow, newRow, highestNewValue)
        }

        if (changeOccurred) {
            updateHistory(cachedGameState)
        }

        return MoveResult(changeOccurred, highestNewValue)
    }

    fun up(): MoveResult {
        var changeOccurred = false
        val cachedGameState = currentGameState.deepCopy()
        var highestNewValue = 0

        for (colIdx in 1 .. getGameState().nrOfColumns) {
            // Determine the new column. Note if it is different from the old one, and maintain the score.
            val column = currentGameState.getColumnAsFilteredList(colIdx)
            val shiftAndCollapseResult = FieldList.shiftCollapseAndCalculateScore(column)
            val shiftedColumn = shiftAndCollapseResult.first
            currentGameState.score += shiftAndCollapseResult.second

            // Remove the old column.
            currentGameState.state.keys.removeIf { it.col == colIdx }

            // Insert the transformed column.
            for (rowIdx in shiftedColumn.indices) {
                val value = shiftedColumn[rowIdx]
                val coor = Coor(rowIdx + 1, colIdx)
                currentGameState.state[coor] = value
            }

            //TODO?~ Maybe this could be done at a later stage.
            // Check if the new column is different from the original column.
            val originalColumn = cachedGameState.getColumn(colIdx)
            val newColumn = currentGameState.getColumn(colIdx)
            changeOccurred = changeOccurred || !equal(originalColumn, newColumn)

            highestNewValue = determineHighestNewValue(originalColumn, newColumn, highestNewValue)
        }

        if (changeOccurred) {
            updateHistory(cachedGameState)
        }

        return MoveResult(changeOccurred, highestNewValue)
    }

    fun down(): MoveResult {
        var changeOccurred = false
        val cachedGameState = currentGameState.deepCopy()
        var highestNewValue = 0

        for (colIdx in 1 .. getGameState().nrOfColumns) {
            // Determine the new column. Also maintain the score.
            val column = currentGameState.getColumnAsReverseFilteredList(colIdx)
            val shiftAndCollapseResult = FieldList.shiftCollapseAndCalculateScore(column)
            val shiftedColumn = shiftAndCollapseResult.first
            currentGameState.score += shiftAndCollapseResult.second

            // Remove the old column.
            currentGameState.state.keys.removeIf { it.col == colIdx }

            // Insert the transformed column.
            // Note that the column is inserted in reverse order, since it was originally retrieved in reverse order.
            val startPos = getGameState().nrOfRows - shiftedColumn.size
            for (rowIdx in startPos until getGameState().nrOfRows) {
                val value = shiftedColumn[ getGameState().nrOfRows - rowIdx  - 1]
                val coor = Coor(rowIdx + 1, colIdx)
                currentGameState.state[coor] = value
            }

            //TODO?~ Maybe this could be done at a later stage.
            // Check if the new column is different from the original column.
            val originalColumn = cachedGameState.getColumn(colIdx)
            val newColumn = currentGameState.getColumn(colIdx)
            changeOccurred = changeOccurred || !equal(originalColumn, newColumn)

            highestNewValue = determineHighestNewValue(originalColumn, newColumn, highestNewValue)        }

        if (changeOccurred) {
            updateHistory(cachedGameState)
        }

        return MoveResult(changeOccurred, highestNewValue)
    }

    //TODO?~ Use List<Int> instead of Map<Int,Int>  ?
    // We may do this after unifying the up/down/left/right code.
    private fun determineHighestNewValue(
        originalRow: Map<Int, Int>,
        newRow: Map<Int, Int>,
        highestKnownValue: Int
    ): Int {
        var result = highestKnownValue
        // Note that, if the original row is NOT empty, then neither will the new row be empty!
        if (originalRow.isNotEmpty()) {
            val highestOriginalValue = originalRow.maxOf { it.value }
            val highestUpdatedValue = newRow.maxOf { it.value }
            if (highestUpdatedValue > highestOriginalValue && highestUpdatedValue > result) {
                result = highestUpdatedValue
            }
        }
        return result
    }

    fun undo() {
        Log.i("2048-game", "Entered GameViewModel.undo()")
        if (historyIndex > 0) {
            Log.i("2048-game", "Decreasing historyIndex from $historyIndex")
            historyIndex--
            this.currentGameState = history[historyIndex].deepCopy()
        }
    }

    fun redo() {
        Log.i("2048-game", "Entered GameViewModel.redo()")
        if (historyIndex < history.size) {
            Log.i("2048-game", "Restoring game state, historyIndex=$historyIndex")
            this.currentGameState = history[historyIndex].deepCopy()
            historyIndex++
        }
    }

    /**
     * Update the history list after the player makes a new move.
     */
    private fun updateHistory(gameState: GameState) {
        // Update the history list.
        if (historyIndex < history.size) {
            history = history.take(historyIndex).toMutableList()
        }
        history.add(gameState) // We don't need to deepCopy(), it already IS a deepCopy().
        historyIndex++

        //TODO?+ Commented out so I don't have to mock them in the unit test.
        //Log.i("2048-game", "History file: ${historyIndex}/${history.size}")
        //Log.i("2048-game", "...last element: $gameState")
    }
}