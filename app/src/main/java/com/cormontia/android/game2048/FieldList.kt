package com.cormontia.android.game2048

class FieldList {

    //TODO?~ Move function above class declaration?
    companion object {

        fun shiftCollapseAndCalculateScore(l: List<Int>) : Pair<List<Int>, Int> {
            val shiftedRow = shiftToFrontAndCollapse(l)
            val score = calculateScore(l)
            return Pair(shiftedRow, score)
        }

        private fun shiftToFrontAndCollapse(l: List<Int>): List<Int> {
            val result = mutableListOf<Int>()
            val aux = l.toMutableList()
            aux.add(0) // Sentinel value
            val input = aux.toIntArray()

            var i = 0
            while (i < input.size - 1) {
                val a1 = input[i]
                val a2 = input[i+1]
                if (a1 == a2) {
                    result.add(a1 + a2)
                    i++
                } else {
                    result.add(a1)
                }
                i++
            }

            return result
        }

        //TODO?~ Add unit test?
        private fun calculateScore(l: List<Int>) = l
                .asSequence()
                .windowed(2,1)
                .filter { it[0] == it[1] }
                .map { it[0] + it[1] }
                .sum()

    }
}