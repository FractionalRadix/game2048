package com.cormontia.android.game2048

class FieldList {

    //TODO!~ Use this for the  "shift-and-collapse" functionality.
    // That way we can give our rows and columns any (constant) length that we desire.

    //TODO?~ Move function above class declaration?
    //TODO?+ Let it return if the output differs from the input? (Caller can also check that now...)
    companion object {

        fun shiftCollapseAndCalculateScore(l: List<Int>) : Pair<List<Int>, Int> {
            val shiftedRow = shiftAndCollapse(l)
            val score = calculateScore(l)
            return Pair(shiftedRow, score)
        }

        private fun shiftAndCollapse(l: List<Int>): List<Int> {
            val stateMachine = StateMachine()

            val result = l
                //.filterNotNull()
                .mapNotNull { stateMachine.receive(it) }
                .toMutableList() as MutableList<Int?>
            result.add(stateMachine.finish())

            //TODO?~ We may not need this HERE.
            //val inputLength = l.size
            //val resultLength = result.size
            //repeat(inputLength - resultLength) { result.add(null) }

            return result
                .filterNotNull() //TODO?- added while trying to make this thing work on List<Int> instead of List<Int?>
        }

        //TODO?~ Add unit test?
        private fun calculateScore(l: List<Int>) = l
                .asSequence()
                //.filterNotNull()
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