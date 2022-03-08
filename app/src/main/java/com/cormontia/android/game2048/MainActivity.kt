package com.cormontia.android.game2048

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var gameState: GameState
    private lateinit var gameBoardView: GameBoardView

    //TODO!~ Use a smaller font for the numbers. (Or maybe even custom graphics...?)
    //TODO!+ Add score.
    //TODO!+ Let player know (and possibly restart) if 2048 is reached.
    //TODO!+ Implement "new game" button.
    //TODO!+ Implement Load, Save, and perhaps Share buttons?
    //TODO!+ Use Material Design icons for the buttons, instead of text.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameState = ViewModelProvider(this)[GameState::class.java]
        gameState.init()
        gameBoardView = findViewById(R.id.gameBoardView)
        gameBoardView.updateGameState(gameState.getGameState())

        //TODO?~ Use View binding to instead of findViewById?
        // (Note that we dropped "android:onClick" in the XML because it's deprecated.
        //  In its method comment it said to use `findViewById`. Now we have to use something else AGAIN...)
        findViewById<Button>(R.id.leftButton).setOnClickListener { left() }
        findViewById<Button>(R.id.rightButton).setOnClickListener { right() }
        findViewById<Button>(R.id.upButton).setOnClickListener { up() }
        findViewById<Button>(R.id.downButton).setOnClickListener { down() }
    }

    fun newGame(view: View) {
        TODO()
    }

    fun load(view: View) {
        TODO()
    }

    fun save(view: View) {
        TODO()
    }

    fun undo(view: View) {
        TODO()
    }

    fun redo(view: View) {
        TODO()
    }

    fun share(view: View) {
        TODO()
    }

    private fun right() {
        val gameStateChanged = gameState.right()
        if (gameStateChanged) {
            gameState.placeNewValue()
        }
        gameBoardView.updateGameState(gameState.getGameState())
    }

    private fun left() {
        val gameStateChanged = gameState.left()
        if (gameStateChanged) {
            gameState.placeNewValue()
        }
        gameBoardView.updateGameState(gameState.getGameState())

    }

    private fun up() {
        val gameStateChanged = gameState.up()
        if (gameStateChanged) {
            gameState.placeNewValue()
        }
        gameBoardView.updateGameState(gameState.getGameState())
    }

    private fun down(){
        val gameStateChanged = gameState.down()
        if (gameStateChanged) {
            gameState.placeNewValue()
        }
        gameBoardView.updateGameState(gameState.getGameState())
    }
}