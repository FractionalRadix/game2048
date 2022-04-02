package com.cormontia.android.game2048

interface FieldList {
    val length: Int
    operator fun set(index: Int, value: Int?)
    operator fun get(index: Int): Int?
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