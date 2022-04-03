package com.cormontia.android.game2048

interface FieldList {
    val length: Int
    operator fun set(index: Int, value: Int?)
    operator fun get(index: Int): Int?

    /**
     * Find the index of the first empty cell in the list, starting from (including) `start`.
     * For example, if the (1-based) list is [2,_,2,_,_,8] then firstEmptyCell(3) will return 4, and firstEmptyCell(5) will return 5.
     * @return The index of the first empty cell from `start`, or the length of the list if there is no such cell.
     */
    fun firstEmptyCellFrom(start: Int): Int {
        var idx = start
        while (idx <= this.length && this[idx] != null)
            idx++
        return idx
    }

    /**
     * Find the index of first non-empty cell in the list, starting from (including) `start`.
     * For example, if the (1-based) list is [2,_,8,4,_,8] then firstNonEmptyCell(1) will return 1, firstNonEmptyCell(4) will return 4.
     */
    fun firstNonEmptyCellFrom(start: Int): Int {
        var idx = start
        while (idx <= this.length && this[idx] == null) {
            idx++
        }
        return idx
    }

    /**
     * Determine the ranges of empty cells in the list.
     * For example, the 1-based list [_,_,_,4,4,_] will yield [(1,3),(6,6)]
     * @return A list of IntRanges, where each IntRange corresponds to a series of 1 or more consecutive empty cells.
     */
    fun determineGaps(): List<IntRange> {
        val gaps = mutableListOf<IntRange>()

        var nextIdx = 1
        do {
            val startOfGap = firstEmptyCellFrom(nextIdx)
            if (startOfGap <= this.length) {
                val endOfGap = firstNonEmptyCellFrom(startOfGap) - 1
                gaps.add(IntRange(startOfGap, endOfGap))
                nextIdx = endOfGap + 2 // endOfGap + 1 is the non-empty cell, so the first candidate for the next EMPTY  cell is endOfGap + 2
            }

        } while (startOfGap <= this.length && nextIdx <= this.length)

        return gaps
    }

}

class RowFieldList(private val gameState: GameState, private val rowIdx: Int, override val length: Int) : FieldList {

    override fun set(index: Int, value: Int?) {
        val idx = Coor(rowIdx, index)
        if (value == null) {
            gameState.state.remove(idx)
        } else {
            gameState.state[idx] = value
        }
    }

    override fun get(index: Int): Int? {
        return gameState.state[Coor(rowIdx, index)]
    }
}

class ReverseRowFieldList(private val gameState: GameState, private val rowIdx: Int, override val length: Int) : FieldList {
    override fun set(index: Int, value: Int?) {
        val idx = Coor(rowIdx, 5 - index)
        if (value == null) {
            gameState.state.remove(idx)
        } else {
            gameState.state[idx] = value
        }
    }

    override fun get(index: Int): Int? {
        return gameState.state[Coor(rowIdx, 5 - index)]
    }
}

class ColumnList(private val gameState: GameState, private val colIdx: Int, override val length: Int): FieldList {
    override fun set(index: Int, value: Int?) {
        val idx = Coor(index, colIdx)
        if (value == null) {
            gameState.state.remove(idx)
        } else {
            gameState.state[idx] = value
        }
    }

    override fun get(index: Int): Int? {
        return gameState.state[Coor(index, colIdx)]
    }
}

class ReverseColumnList(private val gameState: GameState, private val colIdx: Int, override val length: Int): FieldList {
    override fun set(index: Int, value: Int?) {
        val idx = Coor(length + 1 - index, colIdx)
        if (value == null) {
            gameState.state.remove(idx)
        } else {
            gameState.state[idx] = value
        }
    }

    override fun get(index: Int): Int? {
        return gameState.state[Coor(length + 1 - index, colIdx)]
    }
}