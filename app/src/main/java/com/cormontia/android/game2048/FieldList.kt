package com.cormontia.android.game2048

class FieldList {

    //TODO!~ Use this for the  "shift-and-collapse" functionality.
    // That way we can give our rows and columns any (constant) length that we desire.

    //TODO?~ Move function above class declaration?
    //TODO?+ Let it return if the output differs from the input? (Caller can also check that now...)
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

            // Example input: 2,2,2
            // Output: 4,2

            // Example input: 4,4,4,8
            // ...should become 8, 4, 8.

            // Example input: 8,4,4,4
            //  ...should become 8, 8, 4

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

        private fun OLD_shiftAndCollapse(l: List<Int>): List<Int> {
            val stateMachine = StateMachine()

            //TODO!~ We should work from the last element to the first...
            val result = l
                .mapNotNull { stateMachine.receive(it) }
                .toMutableList()
            val last = stateMachine.finish()
            if (last != null)
                result.add(0, last)

            //TODO?~ We may not need this HERE.
            //val inputLength = l.size
            //val resultLength = result.size
            //repeat(inputLength - resultLength) { result.add(null) }

            return result
                .reversed()
        }

        //TODO?~ Add unit test?
        private fun calculateScore(l: List<Int>) = l
                .asSequence()
                .windowed(2,1)
                .filter { it[0] == it[1] }
                .map { it[0] + it[1] }
                .sum()

    }

    class StateMachine() {
        private var state: Int? = null
        private var finished = false

        fun receive(n: Int): Int? {
            if (finished) {
                return null
            }

            if (state == null) {
                state = n
                return null
            } else {
                if (state == n) {
                    state = null
                    return 2 * n
                } else {
                    val oldState = state
                    state = n
                    return oldState
                }
            }
        }

        fun finish(): Int? {
            finished = true
            return state
        }
    }
}