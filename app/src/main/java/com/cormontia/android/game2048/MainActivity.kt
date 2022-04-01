package com.cormontia.android.game2048

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.ViewModelProvider
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameBoardView: GameBoardView
    private lateinit var mGestureDetector: GestureDetectorCompat

    //TODO!~ Use a smaller font for the numbers. (Or maybe even custom graphics...?)
    //TODO!+ Let player know (and possibly restart) if 2048 is reached.
    //TODO!+ Implement Load, Save, and perhaps Share buttons?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        gameViewModel.init()
        gameBoardView = findViewById(R.id.gameBoardView)
        gameBoardView.updateGameState(gameViewModel.getGameState())

        mGestureDetector = GestureDetectorCompat(this, FlingGestureListener())

        //TODO?~ Use View binding to instead of findViewById?
        // (Note that we dropped "android:onClick" in the XML because it's deprecated.
        //  In its method comment it said to use `findViewById`. Now we have to use something else AGAIN...)
        findViewById<ImageButton>(R.id.leftButton).setOnClickListener { left() }
        findViewById<ImageButton>(R.id.rightButton).setOnClickListener { right() }
        findViewById<ImageButton>(R.id.upButton).setOnClickListener { up() }
        findViewById<ImageButton>(R.id.downButton).setOnClickListener { down() }

        findViewById<ImageButton>(R.id.newGameButton).setOnClickListener{ newGame() }
        findViewById<ImageButton>(R.id.undoButton).setOnClickListener{ undo() }
        findViewById<ImageButton>(R.id.redoButton).setOnClickListener{ redo() }

        val scoreText = resources.getString(R.string.score)
        findViewById<TextView>(R.id.scoreView).text = "$scoreText ${gameViewModel.getGameState().score}"
    }

    private fun newGame() {
        gameViewModel.startNewGame()
        gameBoardView.updateGameState(gameViewModel.getGameState())

        val scoreText = resources.getString(R.string.score)
        findViewById<TextView>(R.id.scoreView).text = "$scoreText ${gameViewModel.getGameState().score}"
    }

    fun load() {
        TODO()
    }

    fun save() {
        TODO()
    }

    private fun undo() {
        gameViewModel.undo()
        gameBoardView.updateGameState(gameViewModel.getGameState())
    }

    private fun redo() {
        gameViewModel.redo()
        gameBoardView.updateGameState(gameViewModel.getGameState())
    }

    fun share() {
        TODO()
    }

    // Gesture detection

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    inner class FlingGestureListener : GestureDetector.SimpleOnGestureListener() {
        // Should almost always be overridden to return `true`.
        // "If you return false from onDown(), as GestureDetector.SimpleOnGestureListener does by default,
        //  the system assumes that you want to ignore the rest of the gesture, and the other methods of GestureDetector.OnGestureListener never get called."
        // (Source: https://developer.android.com/training/gestures/detector#detect-a-subset-of-supported-gestures )
        override fun onDown(e: MotionEvent): Boolean {
            Log.i("game-2048", "Entered FlingGestureListener.onDown(...)")
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.i("game-2048", "Entered FlingGestureListener.onFling(...)")

            // Memo: Velocity is in pixels / second.

            val rightward = e2!!.x - e1!!.x
            val upward = - (e2.y - e1.y)

            if (abs(rightward) > abs(upward)) {
                if (rightward > 0) {
                    right()
                } else {
                    left()
                }
                return true // Tell caller that we handled it.

            } else if (abs(upward) > abs(rightward)) {
                if (upward > 0) {
                    up()
                } else {
                    down()
                }
                return true // Tell caller that we handled it.
            }
            // We deliberately ignore when abs(upward)==abs(leftward). We're being generous enough already.
            //  We should consider "if (abs(leftward) > 1.5 * abs(upward))", so as not to allow for ambiguous swipes.

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    // Functions to move the tiles in the model, and update the view accordingly.
    //  These are both called from the gesture detector, and wired to the buttons.

    private fun right() {
        move(fun() = gameViewModel.right() )
    }

    private fun left() {
        move(fun() = gameViewModel.left() )
    }

    private fun up() {
        move(fun() = gameViewModel.up() )
    }

    private fun down() {
        move(fun() = gameViewModel.down() )
    }

    private fun move(moveInModel: () -> Boolean) {
        val gameStateChanged = moveInModel()
        if (gameStateChanged) {
            gameViewModel.placeNewValue()
        }
        gameBoardView.updateGameState(gameViewModel.getGameState())

        val scoreText = resources.getString(R.string.score)
        findViewById<TextView>(R.id.scoreView).text = "$scoreText ${gameViewModel.getGameState().score}"
    }

}