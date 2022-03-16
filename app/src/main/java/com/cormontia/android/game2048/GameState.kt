package com.cormontia.android.game2048

data class GameState(val state: MutableMap<Coor, Int>) {
    /**
     * Make a deep copy of the current game state.
     * @return A new GameState, that is a copy of this one, but does not share state with this one.
     */
    fun deepCopy(): GameState {
        val result = mutableMapOf<Coor, Int>()
        state.forEach { result[it.key] = it.value }
        return GameState(result)
    }

    /**
     * Given a row index, return a mapping from its row numbers to its contents.
     * For example, if the 2nd row reads "_,8,16,_", then `getRow(2)` will return the map { 2 -> 8, 3 -> 16 }.
     * @param rowIdx of the row (1-based).
     * @return The contents of the row, as a mapping from field indices to field contents.
     */
    fun getRow(rowIdx: Int) = state
        .filterKeys { it.row == rowIdx }
        .map { Pair(it.key.col, it.value) }
        .toMap()

    /**
     * Given a column index, return a mapping from its row numbers to its contents.
     * For example, if the 3th column reads "2,4,_,2", then `getColumn(3)` will return the map { 1 -> 2, 2 -> 4, 4 -> 2 }.
     * @param colIdx of the column (1-based).
     * @return The contents of the column, as a mapping from field indices to field contents.
     */
    fun getColumn(colIdx: Int) = state
        .filterKeys { it.col == colIdx }
        .map { Pair(it.key.row, it.value) }
        .toMap()
}
