package com.cormontia.android.game2048

import org.junit.Assert.*
import org.junit.Test

class GameStateTests {
    @Test
    fun testShiftAndCollapse_shiftWithoutDoubling() {
        // _,4,16,_ becomes _,_,4,16
        val input = mapOf(Pair(2,4), Pair(3,16))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(2, output.size)
        assert(output[3] == 4)
        assert(output[4] == 16)
    }

    @Test
    fun testShiftAndCollapse_simpleShiftAndDoubling() {
        // 2,2,_,_ becomes _,_,_,4
        val input = mapOf(Pair(1,2), Pair(2,2))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(1, output.size)
        assert(output[4] == 4)
    }

    @Test
    fun testShiftAndCollapse_fieldOnlyAddedOnce() {
        // 2,2,2,_ becomes _,_,2,4
        val input = mapOf(Pair(1,2), Pair(2,2), Pair(3,2))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(2, output.size)
        assert(output[4] == 4)
        assert(output[3] == 2)
    }

    @Test
    fun testShiftAndCollapse_fieldsWithDistanceBetweenThemCollapseIntoOne() {
        // 4,_,_,4 becomes _,_,_,8
        val input = mapOf(Pair(1,4), Pair(4,4))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(1, output.size)
        assert(output[4] == 8)
    }

    @Test
    fun testShiftAndCollapse_fourTwosBecomeTwoFours() {
        // 2,2,2,2 becomes _,_,4,4
        val input = mapOf(Pair(1,2), Pair(2,2), Pair(3,2), Pair(4,2))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(2, output.size)
        assert(output[3] == 4)
        assert(output[4] == 4)
    }

    @Test
    fun testReverseRowOrColumn() {
        // _,2,_,8 becomes 8,_,2,_
        val input = mutableMapOf<Int,Int>()
        input[2] = 2
        input[4] = 8
        val output = GameViewModel.StaticMethods.reverseRowOrColumn(input)
        assertEquals(output.size,2)
        assert(output[1]==8)
        assert(output[3]==2)
    }

    @Test
    fun testShift_whenLastFieldIsEmpty() {
        // _,4,8,_  shifts to _,_,4,8
        val input = mutableMapOf<Int, Int>()
        input[2]=4
        input[3]=8
        val output = GameViewModel.StaticMethods.shift(input)
        assertEquals(output.size,2)
        assert(output[3] == 4)
        assert(output[4] == 8)
    }

    @Test
    fun testShift_withSpaceBetweenFields() {
        // 2,_,4,_ shifts to _,_,2,4
        val input = mutableMapOf<Int, Int>()
        input[1]=2
        input[3]=4
        val output = GameViewModel.StaticMethods.shift(input)
        assertEquals(output.size,2)
        assert(output[3] == 2)
        assert(output[4] == 4)
    }
}