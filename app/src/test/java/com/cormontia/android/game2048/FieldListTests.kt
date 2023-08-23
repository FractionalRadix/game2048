package com.cormontia.android.game2048

import org.junit.Test

class FieldListTests {
    @Test
    fun testShiftAndCollapse_shiftWithoutDoubling() {
        // _,4,16,_ becomes 4,16,_,_
        //val input = listOf(null,4,16,null)
        val input = listOf(4,16)
        val output = FieldList.shiftCollapseAndCalculateScore(input).first
        //assert(output.equals(listOf(4,16,null,null)))
        assert(output.equals(listOf(4,16)))

        // We CAN use "equals" for lists...! https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-abstract-list/equals.html
        /*
        assert(output[1] == 4)
        assert(output[2] == 16)
        assert(output[3] == null)
        assert(output[4] == null)
         */
    }

    @Test
    fun testShiftAndCollapse_simpleShiftAndDoubling() {
        // 2,2,_,_ becomes 4,_,_,_
        //val input = listOf(2,2,null,null)
        val input = listOf(2,2)
        val output = FieldList.shiftCollapseAndCalculateScore(input).first
        //assert(output.equals(listOf(4,null,null,null)))
        assert(output.equals(listOf(4)))

        /*
        assert(output[1] == 4)
        assert(output[2] == null)
        assert(output[3] == null)
        assert(output[4] == null)
         */
    }

    @Test
    fun testShiftAndCollapse_fieldOnlyAddedOnce() {
        // 2,2,2,_ becomes 2,4,_,_
        //val input = listOf(2,2,2,null)
        val input = listOf(2,2,2)
        val output = FieldList.shiftCollapseAndCalculateScore(input).first
        //assert(output.equals(listOf(4,2,null,null)))
        assert(output.equals(listOf(4,2)))

        /*
        assert(output[1] == 4)
        assert(output[2] == 2)
        assert(output[3] == null)
        assert(output[4] == null)
         */
    }

    @Test
    fun testShiftAndCollapse_fieldsWithDistanceBetweenThemCollapseIntoOne() {
        // 4,_,_,4 becomes 8,_,_,_
        //val input = listOf(4,null,null,4)
        val input = listOf(4,4)
        val output = FieldList.shiftCollapseAndCalculateScore(input).first
        //assert(output.equals(listOf(8, null, null, null)))
        assert(output.equals(listOf(8)))

        /*
        assert(output[1] == 8)
        assert(output[2] == null)
        assert(output[3] == null)
        assert(output[4] == null)
         */
    }

    @Test
    fun testShiftAndCollapse_fourTwosBecomeTwoFours() {
        // 2,2,2,2 becomes 4,4,_,_
        val input = listOf(2,2,2,2)
        val output = FieldList.shiftCollapseAndCalculateScore(input).first
        //assert(output.equals(listOf(4,4,null,null)))
        assert(output.equals(listOf(4,4)))

        /*
        assert(output[1] == 4)
        assert(output[2] == 4)
        assert(output[3] == null)
        assert(output[4] == null)
         */
    }

    // Add a test for a bad case in our new system:
    // 2,2,4,_   Because first 2,2 collapses to 4, but that must result in 4,4,_,_ . The new four should NOT collapse again!
    @Test
    fun testShiftAndCollapse_fieldsOnlyCollapseOnce() {
        //val input = listOf(2,2,4,null)
        val input = listOf(2,2,4)
        val output = FieldList.shiftCollapseAndCalculateScore(input).first
        //assert(output.equals(listOf(4,4,null,null)))
        assert(output.equals(listOf(4,4)))

        /*
        assert(output[1] == 4)
        assert(output[2] == 4)
        assert(output[3] == null)
        assert(output[4] == null)
         */
    }

    //TODO?- Remove if we don't use null values in shift-collapse-calculateScore anymore.
    //@Test
    //fun testAllNullsYieldsEquallyManyNulls() {
    //    val input = listOf(null, null, null, null)
    //    val output = FieldList.shiftCollapseAndCalculateScore(input).first
    //    assert(output.equals(listOf(null,null,null,null)))
    //}

    @Test
    fun testLongerShiftAndCollapse() {
        //val input = mutableListOf(8,8,null,null,3,1,4)
        val input = mutableListOf(8,8,3,1,4)
        val output = FieldList.shiftCollapseAndCalculateScore(input).first
        //assert(output.equals(listOf(16,3,1,4,null,null, null)))
        assert(output.equals(listOf(16,3,1,4)))

        //on [8,8,_,_,3,1] results in [16,3,1,_,_,_]
        /*
        assert(list[1] == 8)
        assert(list[2] == 8)
        assert(list[3] == 3)
        assert(list[4] == 1)
        assert(list[5] == null)
        assert(list[6] == null)
         */
    }
}