package com.cormontia.android.game2048

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var gameState: GameState
    private lateinit var gameBoardView: GameBoardView

    //TODO!+ Add score.
    //TODO!+ Let player know (and possibly restart) if 2048 is reached.
    //TODO!+ Add "new game" button.
    //TODO?+ Add Load, Save, and perhaps Share buttons?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameState = ViewModelProvider(this)[GameState::class.java]
        gameState.init()
        gameBoardView = findViewById(R.id.gameBoardView)
        gameBoardView.updateGameState(gameState.getGameState())
    }

    fun right(view: View) {
        val gameStateChanged = gameState.right()
        if (gameStateChanged) {
            gameState.placeNewValue()
        }
        gameBoardView.updateGameState(gameState.getGameState())
    }

    fun left(view: View) {
        val gameStateChanged = gameState.left()
        if (gameStateChanged) {
            gameState.placeNewValue()
        }
        gameBoardView.updateGameState(gameState.getGameState())

    }

    fun up(view: View) {
        val gameStateChanged = gameState.up()
        if (gameStateChanged) {
            gameState.placeNewValue()
        }
        gameBoardView.updateGameState(gameState.getGameState())
    }

    fun down(view: View){
        val gameStateChanged = gameState.down()
        if (gameStateChanged) {
            gameState.placeNewValue()
        }
        gameBoardView.updateGameState(gameState.getGameState())
    }
}