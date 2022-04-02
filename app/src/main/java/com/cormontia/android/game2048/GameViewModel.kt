package com.cormontia.android.game2048

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.HashMap

data class Coor(val row: Int, val col:Int)

//TODO!~ We need TWO game-states here.
// The present game state, and the game state that is being built from the player's move.
// That saves a lot of deep-copying.
// Also: note that if we switch to using "FieldList", a lot of the changes will be in-place.

class GameViewModel : ViewModel() {
    private var currentGameState = GameState(HashMap(),0)
    private val random = Random()

    private var history = mutableListOf<GameState>()
    private var historyIndex = 0

    /**
     * Check if the game has only just started, or if `init` is called due to an orientation change.
     */
    private var startOfGame = true

    companion object StaticMethods {

        fun shift(row: Map<Int, Int>): MutableMap<Int, Int> {
            // Move all elements as far to the right as possible.
            // Do NOT collapse anything.
            val shiftedRow = mutableMapOf<Int,Int>()

            row.forEach { shiftedRow[it.key] = it.value}

            // First, move every field as far to the right as possible.
            if (shiftedRow[4] == null) {
                if (shiftedRow[3] != null) {
                    shiftedRow[4] = shiftedRow[3]!!
                    shiftedRow.remove(3)
                } else if (shiftedRow[2] != null) {
                    shiftedRow[4] = shiftedRow[2]!!
                    shiftedRow.remove(2)
                } else if (shiftedRow[1] != null) {
                    shiftedRow[4] = shiftedRow[1]!!
                    shiftedRow.remove(1)
                }
            }

            if (shiftedRow[3] == null) {
                if (shiftedRow[2] != null) {
                    shiftedRow[3] = shiftedRow[2]!!
                    shiftedRow.remove(2)
                } else if (shiftedRow[1] != null) {
                    shiftedRow[3] = shiftedRow[1]!!
                    shiftedRow.remove(1)
                }
            }

            if (shiftedRow[2] == null) {
                if (shiftedRow[1] != null) {
                    shiftedRow[2] = shiftedRow[1]!!
                    shiftedRow.remove(1)
                }
            }

            return shiftedRow
        }

        //TODO?~ Do I really want the new "shiftAndCollapse" to shift-and-collapse IN-PLACE?
        //NOTE: this one works SOMEWHAT, it doesn't handle gaps.
        fun OLD_shiftAndCollapse(list: FieldList): FieldList {
            // The following works but only shifts the list by ONE!
            for (i in 1 until list.length) {
                if (list[i] == null) {
                    list[i] = list[i + 1]
                    list[i + 1] = null
                } else if (list[i] == list[i + 1]) {
                    list[i] = 2 * list[i]!!
                    list[i + 1] = null
                }

                //TODO!+ To make this effective, you should now check how many empty fields there are.
                // And shift by THAT length ("i + gapSize") instead of 1 ("i + 1")
                //   TEST: 8,_,_,_,16 should become 8,16
                // gapSize should be 3.

            }

            return list
        }

        /**
         * Find the index of the first empty cell in the list, starting from (including) `start`.
         * For example, if the (1-based) list is [2,_,2,_,_,8] then firstEmptyCell(3) will return 4, and firstEmptyCell(5) will return 5.
         * @return The index of the first empty cell from `start`, or the length of the list if there is no such cell.
         */
        fun firstEmptyCellFrom(list: FieldList, start: Int): Int {
            val firstEmpty: Int
            var idx = start
            while (idx <= list.length && list[idx] != null)
                idx++
            return idx
        }

        /**
         * Find the index of first non-empty cell in the list, starting from (including) `start`.
         * For example, if the (1-based) list is [2,_,8,4,_,8] then firstNonEmptyCell(1) will return 1, firstNonEmptyCell(4) will return 4.
         */
        fun firstNonEmptyCellFrom(list: FieldList, start: Int): Int {
            var idx = start
            while (idx <= list.length && list[idx] == null) {
                idx++
            }
            return idx
        }

        /**
         * Given a FieldList, determine the ranges of empty cells in them.
         * For example, the 1-based list [_,_,_,4,4,_] will yield [(1,3),(6,6)]
         * @param list A FieldList that may contain empty fields, i.e. fields that are <code>null</code>.
         * @return A list of IntRanges, where each IntRange corresponds to a series of 1 or more consecutive empty cells.
         */
        fun determineGaps(list: FieldList): List<IntRange> {
            val gaps = mutableListOf<IntRange>()

            var nextIdx = 1
            do {
                val startOfGap = firstEmptyCellFrom(list, nextIdx)
                if (startOfGap <= list.length) {
                    val endOfGap = firstNonEmptyCellFrom(list, startOfGap) - 1
                    gaps.add(IntRange(startOfGap, endOfGap))
                    nextIdx = endOfGap + 2 // endOfGap + 1 is the non-empty cell, so the first candidate for the next EMPTY  cell is endOfGap + 2
                }

            } while (startOfGap <= list.length && nextIdx <= list.length)

            return gaps
        }

        fun shiftAndCollapse(list: FieldList): FieldList {
            val gaps = determineGaps(list)

            //TODO!+ Now that you know the gaps, shift over them.
            // And keep in mind to merge blocks if they have the same value.
            // Well, let's first just empty the gaps.

            // Suppose we have [2,_,_,4].
            // This is a single gap, (2,3).
            // So, everything after 3 must be shifted by the size of this gap.

            TODO()

            return list
        }

        /**
         * Transform a row or column according to the rules of 2048.
         * For ease of understanding, assume that the input is a row, and that we're shifting it to the right.
         * Each entry in the input maps a field in the row to its index.
         * If a field does not have an entry then that field is empty.
         * If two adjacent fields have the same value, they are collapsed into one that has the sum of their values.
         * (E.g. 2,2,8,32 would become _,4,8,32).
         *
         * To use this to shift leftwards, simply reverse the row to transform, apply this function, then reverse it back.
         * A similar thing applies to using this method for columns.
         *
         * @param row A mapping of row- or column indexes to field contents.
         * @return A pair of elements.
         * The first is a new row, where all elements are shifted as far to the highest position as possible, with adjacent equal fields collapsed int one.
         * The second is the "score", the value of all elements that were merged, summed together.
         */
        fun shiftAndCollapse(row: Map<Int, Int>): Pair<Map<Int, Int>, Int> {

            var score = 0

            // First, shift all the elements as far to the end as you can.
            var shiftedRow = shift(row)

            // Second, if two adjacent fields have the same value, collapse them into one.
            if (shiftedRow[4] != null && shiftedRow[4] == shiftedRow[3]) {
                val sum = shiftedRow[4]!! + shiftedRow[3]!!
                shiftedRow[4] = sum
                shiftedRow.remove(3)
                score += sum
                if (sum >= 2048) {
                    //TODO!+ We have a winner!
                }
            }
            if (shiftedRow[3] != null && shiftedRow[3] == shiftedRow[2]) {
                val sum = shiftedRow[3]!! + shiftedRow[2]!!
                shiftedRow[3] = sum
                shiftedRow.remove(2)
                score += sum
                if (sum >= 2048) {
                    //TODO!+ We have a winner!
                }
            }
            if (shiftedRow[2] != null && shiftedRow[2] == shiftedRow[1]) {
                val sum = shiftedRow[2]!! + shiftedRow[1]!!
                shiftedRow[2] = sum
                shiftedRow.remove(1)
                score += sum
                if (sum >= 2048) {
                    //TODO!+ We have a winner!
                }
            }

            shiftedRow = shift(shiftedRow)

            return Pair(shiftedRow, score)
        }

        /**
         * Given the values in a row or column, revert them.
         * For example, [_,2,_,8] becomes [8,_,2_]
         */
        fun reverseRowOrColumn(list: Map<Int,Int>): Map<Int, Int> {
            val reversedList = mutableMapOf<Int,Int>()
            list.forEach {
                reversedList[5-it.key] = it.value
            }
            return reversedList
        }

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
        currentGameState = GameState(HashMap(), 0)
        placeNewValue()

        historyIndex = 0
        history.clear()
    }

    //TODO?~ Use an Observable...?
    fun getGameState(): GameState {
        return currentGameState
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
        for (rowIdx in 1..4) {
            for (colIdx in 1..4) {
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
    private fun movesAvailable(): Boolean {
        // 1. If there is at least 1 open field, then the user can make a move.
        if (!findEmptyPositions().isEmpty())
            return true
        // 2. If there are at least 2 fields with the same value adjacent to each other, then the user can make a move.
        for (rowIdx in 1..4) {
            for (colIdx in 1..3) {
                if (currentGameState.state[Coor(rowIdx, colIdx)] == currentGameState.state[Coor(rowIdx, colIdx + 1)])
                    return true
            }
        }

        for (colIdx in 1..4) {
            for (rowIdx in 1..3) {
                if (currentGameState.state[Coor(rowIdx, colIdx)] == currentGameState.state[Coor(rowIdx + 1, colIdx)])
                    return true
            }
        }

        // 3. If neither of these conditions hold, then the playe rhas no more moves available.
        return false
    }

    /**
     * Adjust the game board if the player moves values to the right.
     * @return <code>true</code> if and only if this move caused a change in the game board.
     */
    fun right(): Boolean {
        var changeOccurred = false
        val cachedGameState = currentGameState.deepCopy()

        for (rowIdx in 1..4) {
            val row = currentGameState.getRow(rowIdx)
            val shiftedRow = shiftAndCollapse(row)
            if (!equal(row, shiftedRow.first)) {
                changeOccurred = true
            }
            currentGameState.score += shiftedRow.second

            // Remove the old row.
            currentGameState.state.keys.removeIf { it.row == rowIdx }
            // Insert the transformed row.
            for (elt in shiftedRow.first) {
                currentGameState.state[Coor(rowIdx, elt.key)] = elt.value
            }
        }

        if (changeOccurred) {
            updateHistory(cachedGameState)
        }

        return changeOccurred
    }

    /**
     * Adjust the game board if the player moves values to the left.
     * @return <code>true</code> if and only if this move caused a change in the game board.
     */
    fun left(): Boolean {
        var changeOccurred = false
        val cachedGameState = currentGameState.deepCopy()

        for (rowIdx in 1..4) {
            val row = currentGameState.getRow(rowIdx)

            val shiftedRow = shiftAndCollapse(reverseRowOrColumn(row))
            if (!equal(reverseRowOrColumn(row), shiftedRow.first)) {
                changeOccurred = true
            }
            currentGameState.score += shiftedRow.second

            // Remove the old row.
            currentGameState.state.keys.removeIf { it.row == rowIdx }
            // Insert the transformed row, but in reverse order.
            for (elt in shiftedRow.first) {
                currentGameState.state[Coor(rowIdx, 5 - elt.key)] = elt.value
            }
        }

        if (changeOccurred) {
            updateHistory(cachedGameState)
        }

        return changeOccurred
    }

    /**
     * Adjust the game board if the player moves values up.
     * @return <code>true</code> if and only if this move caused a change in the game board.
     */
    fun up(): Boolean {
        var changeOccurred = false
        val cachedGameState = currentGameState.deepCopy()

        for (colIdx in 1..4) {
            val col = currentGameState.getColumn(colIdx)
            val shiftedCol = shiftAndCollapse(reverseRowOrColumn(col))

            if (!equal(reverseRowOrColumn(col), shiftedCol.first)) {
                changeOccurred = true
            }
            currentGameState.score += shiftedCol.second

            // Remove the old column.
            currentGameState.state.keys.removeIf { it.col == colIdx }
            // Insert the transformed column, but in reverse order.
            for (elt in shiftedCol.first) {
                currentGameState.state[Coor(5 - elt.key, colIdx)] = elt.value
            }
        }

        if (changeOccurred) {
            updateHistory(cachedGameState)
        }

        return changeOccurred
    }

    /**
     * Adjust the game board if the player moves values down.
     * @return <code>true</code> if and only if this move caused a change in the game board.
     */
    fun down(): Boolean {
        var changeOccurred = false
        val cachedGameState = currentGameState.deepCopy()

        for (colIdx in 1..4) {
            val col = currentGameState.getColumn(colIdx)
            val shiftedCol = shiftAndCollapse(col)

            if (!equal(col, shiftedCol.first)) {
                changeOccurred = true
            }
            currentGameState.score += shiftedCol.second

            // Remove the old column.
            currentGameState.state.keys.removeIf { it.col == colIdx }
            // Insert the transformed column.
            for (elt in shiftedCol.first) {
                currentGameState.state[Coor(elt.key, colIdx)] = elt.value
            }
        }

        if (changeOccurred) {
            updateHistory(cachedGameState)
        }

        return changeOccurred
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

        Log.i("2048-game", "History file: ${historyIndex}/${history.size}")
        Log.i("2048-game", "...last element: $gameState")
    }
}