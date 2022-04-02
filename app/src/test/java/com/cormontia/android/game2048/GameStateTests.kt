package com.cormontia.android.game2048

import org.junit.Assert.*
import org.junit.Test

class GameStateTests {

    //TODO?~ Replace with a REAL mock? Use Mockito?
    class MockFieldList(private var v1: Int?, private var v2: Int?, private var v3: Int?, private var v4: Int?): FieldList {
        override fun get(index: Int): Int? {
            return when (index) {
                1 -> v1
                2 -> v2
                3 -> v3
                4 -> v4
                else -> null
            }
        }
        override fun set(index: Int, value: Int?) {
            when (index) {
                1 -> v1 = value
                2 -> v2 = value
                3 -> v3 = value
                4 -> v4 = value
            }
        }
    }

    @Test
    fun testShiftAndCollapse_shiftWithoutDoubling() {
        // _,4,16,_ becomes _,_,4,16
        val input = mapOf(Pair(2,4), Pair(3,16))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(2, output.first.size)
        assert(output.first[3] == 4)
        assert(output.first[4] == 16)
    }

    @Test
    fun testShiftAndCollapse_shiftWithoutDoubling_fieldList() {
        // _,4,16,_ becomes 4,16,_,_
        val input = MockFieldList(null,4,16,null)
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assert(output[1] == 4)
        assert(output[2] == 16)
        assert(output[3] == null)
        assert(output[4] == null)
    }

    @Test
    fun testShiftAndCollapse_simpleShiftAndDoubling() {
        // 2,2,_,_ becomes _,_,_,4
        val input = mapOf(Pair(1,2), Pair(2,2))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(1, output.first.size)
        assert(output.first[4] == 4)
    }

    @Test
    fun testShiftAndCollapse_simpleShiftAndDoubling_fieldList() {
        // 2,2,_,_ becomes 4,_,_,_
        val input = MockFieldList(2,2,null,null)
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assert(output[1] == 4)
        assert(output[2] == null)
        assert(output[3] == null)
        assert(output[4] == null)
    }

    @Test
    fun testShiftAndCollapse_fieldOnlyAddedOnce() {
        // 2,2,2,_ becomes _,_,2,4
        val input = mapOf(Pair(1,2), Pair(2,2), Pair(3,2))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(2, output.first.size)
        assert(output.first[4] == 4)
        assert(output.first[3] == 2)
    }

    @Test
    fun testShiftAndCollapse_fieldOnlyAddedOnce_fieldList() {
        // 2,2,2,_ becomes 2,4,_,_
        val input = MockFieldList(2,2,2,null)
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assert(output[1] == 4)
        assert(output[2] == 2)
        assert(output[3] == null)
        assert(output[4] == null)
    }

    @Test
    fun testShiftAndCollapse_fieldsWithDistanceBetweenThemCollapseIntoOne() {
        // 4,_,_,4 becomes _,_,_,8
        val input = mapOf(Pair(1,4), Pair(4,4))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(1, output.first.size)
        assert(output.first[4] == 8)
    }

    @Test
    fun testShiftAndCollapse_fieldsWithDistanceBetweenThemCollapseIntoOne_fieldList() {
        // 4,_,_,4 becomes 8,_,_,_
        val input = MockFieldList(4,null,null,4)
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assert(output[1] == 8)
        assert(output[2] == null)
        assert(output[3] == null)
        assert(output[4] == null)
    }

    @Test
    fun testShiftAndCollapse_fourTwosBecomeTwoFours() {
        // 2,2,2,2 becomes _,_,4,4
        val input = mapOf(Pair(1,2), Pair(2,2), Pair(3,2), Pair(4,2))
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assertEquals(2, output.first.size)
        assert(output.first[3] == 4)
        assert(output.first[4] == 4)
    }

    @Test
    fun testShiftAndCollapse_fourTwosBecomeTwoFours_fieldList() {
        // 2,2,2,2 becomes 4,4,_,_
        val input = MockFieldList(2,2,2,2)
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assert(output[1] == 4)
        assert(output[2] == 4)
        assert(output[3] == null)
        assert(output[4] == null)
    }

    // Add a test for a bad case in our new system:
    // 2,2,4,_   Because first 2,2 collapses to 4, but that must result in 4,4,_,_ . The new four should NOT collapse again!
    @Test
    fun testShiftAndCollapse_fieldsOnlyCollapseOnce_fieldList() {
        val input = MockFieldList(2,2,4,null)
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assert(output[1] == 4)
        assert(output[2] == 4)
        assert(output[3] == null)
        assert(output[4] == null)
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