package com.cormontia.android.game2048

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameBoardView: GameBoardView

    //TODO!~ Use a smaller font for the numbers. (Or maybe even custom graphics...?)
    //TODO!+ Add score.
    //TODO!+ Let player know (and possibly restart) if 2048 is reached.
    //TODO!+ Implement Load, Save, and perhaps Share buttons?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        gameViewModel.init()
        gameBoardView = findViewById(R.id.gameBoardView)
        gameBoardView.updateGameState(gameViewModel.getGameState())

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
    }

    private fun newGame() {
        gameViewModel.startNewGame()
        gameBoardView.updateGameState(gameViewModel.getGameState())
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
    }
}