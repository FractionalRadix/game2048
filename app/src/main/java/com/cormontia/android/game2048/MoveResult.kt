package com.cormontia.android.game2048

/*
 * Holds the result of a single move - shifting (and possibly collapsing) all rows or columns.
 */
data class MoveResult(val changeOccurred: Boolean, val highestNewValue: Int)