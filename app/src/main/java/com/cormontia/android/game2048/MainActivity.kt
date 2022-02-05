package com.cormontia.android.game2048

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var gameState: GameState
    private lateinit var gameBoardView: GameBoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameState = ViewModelProvider(this)[GameState::class.java]

        gameBoardView = findViewById(R.id.gameBoardView)
    }

    fun right(view: View) {
        gameState.right()
        gameState.placeNewValue() //TODO!~ ONLY if the game state changed!
        gameBoardView.updateGameState(gameState.getGameState())
    }

    fun left(view: View) {
        gameState.left()
        gameState.placeNewValue() //TODO!~ ONLY if the game state changed!
        gameBoardView.updateGameState(gameState.getGameState())

    }

    fun up(view: View) {
        gameState.up()
        gameState.placeNewValue() //TODO!~ ONLY if the game state changed!
        gameBoardView.updateGameState(gameState.getGameState())
    }

    fun down(view: View){
        gameState.down()
        gameState.placeNewValue() //TODO!~ ONLY if the game state changed!
        gameBoardView.updateGameState(gameState.getGameState())
    }
}