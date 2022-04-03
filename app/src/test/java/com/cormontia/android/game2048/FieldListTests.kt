package com.cormontia.android.game2048

import org.junit.Assert
import org.junit.Test

class FieldListTests {
    //TODO?~ Replace with a REAL mock? Use Mockito?
    class MockFieldList(private var v1: Int?, private var v2: Int?, private var v3: Int?, private var v4: Int?): FieldList {
        override val length: Int = 4

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

    //TODO?~ When we look at the FieldList interface this way, we might as well throw the thing away, and just use a MutableList<Int?> instead.....
    class MockFieldList2(private var list: MutableList<Int?>): FieldList {
        override val length = list.size
        override fun get(index: Int): Int? {
            return list[index - 1]
        }
        override fun set(index: Int, value: Int?) {
            list[index - 1] = value
        }
    }

    @Test
    fun testDetermineGaps1() {
        val input = MockFieldList2(mutableListOf(null,null,null,4,8,null,8))
        val actual = input.determineGaps()
        assert(actual.size == 2)
        Assert.assertEquals(actual[0], IntRange(1, 3))
        Assert.assertEquals(actual[1], IntRange(6, 6))
    }

    @Test
    fun testDetermineGaps2() {
        val input = MockFieldList2(mutableListOf(8,null,null,4,8,null,8,null))
        val actual = input.determineGaps()
        assert(actual.size == 3)
        Assert.assertEquals(actual[0], IntRange(2, 3))
        Assert.assertEquals(actual[1], IntRange(6, 6))
        Assert.assertEquals(actual[2], IntRange(8, 8))
    }

    @Test
    fun testShiftAndCollapse_shiftWithoutDoubling() {
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
    fun testShiftAndCollapse_fieldsOnlyCollapseOnce() {
        val input = MockFieldList(2,2,4,null)
        val output = GameViewModel.StaticMethods.shiftAndCollapse(input)
        assert(output[1] == 4)
        assert(output[2] == 4)
        assert(output[3] == null)
        assert(output[4] == null)
    }

}