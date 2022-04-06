package com.cormontia.android.game2048

/**
 * Holds the result of shifting (and possibly collapsing) a single row or column.
 */
class ShiftAndCollapseResult(val newRow: Map<Int, Int>, val score: Int, val highestNewValue: Int)