package com.cormontia.android.game2048

data class GameState(val nrOfRows: Int, val nrOfColumns: Int, val state: MutableMap<Coor, Int>, var score: Int) {
    /**
     * Make a deep copy of the current game state.
     * @return A new GameState, that is a copy of this one, but does not share state with this one.
     */
    fun deepCopy(): GameState {
        val result = mutableMapOf<Coor, Int>()
        state.forEach { result[it.key] = it.value }
        return GameState(nrOfRows, nrOfColumns, result, score)
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

    fun getRowAsFilteredList(rowIdx: Int) : List<Int> {
        return state
            .filterKeys { it.row == rowIdx }
            .toSortedMap( compareBy { it.col } )
            .map { it.value }
    }

    fun getRowAsReverseFilteredList(rowIdx: Int) : List<Int> {
        return state
            .filterKeys { it.row == rowIdx }
            .toSortedMap( compareByDescending { it.col } )
            .map { it.value }
    }

    fun getColumnAsFilteredList(colIdx: Int) : List<Int> {
        return state
            .filterKeys { it.col == colIdx }
            .toSortedMap( compareBy { it.row } )
            .map { it.value }
    }

    fun getColumnAsReverseFilteredList(colIdx: Int) : List<Int> {
        return state
            .filterKeys { it.col == colIdx }
            .toSortedMap( compareByDescending { it.row } )
            .map { it.value }
    }

    //TODO!~ Either list "null" as "null" instead of "0", or don't make this an override of toString().
    // Right now it is confusing.
    fun serialize(): String {
        // Game state is serialized as: [nr of rows, nr of columns, score, value of position (row-1,col-1), value of position (row-1, col-2)... value of position (row-4, col-4) ]
        // If a position is empty, we use a 0 (zero).
        var idx = 0
        // Nr of elements: nr of rows (1 field), nr of columns (1 field), score (1 field), values (nr of fields = nr of rows * nr of columns)
        val intArray = Array(3 + nrOfRows * nrOfColumns) { _ -> 0 }

        intArray[idx++] = nrOfRows
        intArray[idx++] = nrOfColumns
        intArray[idx++] = score
        for (rowIdx in 1..nrOfRows) {
            for (colIdx in 1..nrOfColumns) {
                val coor = Coor(rowIdx, colIdx)
                val value = this.state[coor]
                intArray[idx++] = value ?: 0
            }
        }

        return intArray.joinToString(separator = ",")
    }

    fun deserialize(str: String): GameState {
        //TODO!+ Add "nrOfRows, nrOfColumns" to the serialization!
        val values = str
            .split(",")
            .map { it.toInt() } //TODO!+ Handle NumberFormatException... and add a unit test for that case.

        val nrOfRows = values[0]
        val nrOfColumns = values[1]
        val score = values[2]
        val fields = mutableMapOf<Coor,Int>()
        var rowIdx = 1
        var colIdx = 1
        for (i in 3 until values.size) {
            val currentValue = values[i]

            if (currentValue != 0) {
                val key = Coor(rowIdx, colIdx)
                fields[key] = currentValue
            }

            colIdx++
            if (colIdx > nrOfColumns) {
                rowIdx++
                colIdx = 1
            }

        }

        return GameState(nrOfRows, nrOfColumns, fields, score)
    }
}
