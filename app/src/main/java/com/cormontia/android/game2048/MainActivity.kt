package com.cormontia.android.game2048

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.ViewModelProvider
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameBoardView: GameBoardView
    private lateinit var mGestureDetector: GestureDetectorCompat

    //TODO?+ Implement Share buttons? Or drop it?

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
        findViewById<ImageButton>(R.id.saveButton).setOnClickListener { save() }
        findViewById<ImageButton>(R.id.loadButton).setOnClickListener { load() }

        updateView()
    }

    private fun newGame() {
        gameViewModel.startNewGame()
        updateView()
        showWinningBanner(false) //TODO?~ Shouldn't this be in the "updateView" somehow...?
    }

    private val storageFileMimeType = "text/plain"

    private val loadCode = 14
    private fun load() {
        val loadIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        loadIntent.addCategory(Intent.CATEGORY_OPENABLE)
        loadIntent.type = storageFileMimeType
        startActivityForResult(loadIntent, loadCode) //TODO!~ Use "registerForActivityResult" instead.
    }

    private val saveCode = 21
    private fun save() {
        // "Note: ACTION_CREATE_DOCUMENT cannot overwrite an existing file.
        //  If your app tries to save a file with the same name, the system appends a number in parentheses at the end of the file name."
        // Source: https://developer.android.com/training/data-storage/shared/documents-files#create-file
        val saveIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        saveIntent.type = storageFileMimeType
        saveIntent.putExtra(Intent.EXTRA_TITLE, "state2048.txt") //TODO?~ Add timestamp or something to make it unique?
        startActivityForResult(saveIntent, saveCode) //TODO!~ Use "registerForActivityResult" instead.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        val uri = resultData?.data
        when (requestCode) {
            saveCode -> {
                if (uri != null) {
                    //TODO!+ Handle FileNotFoundException and IOException
                    val contentResolver = applicationContext.contentResolver
                    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w")
                    val fos = FileOutputStream(parcelFileDescriptor?.fileDescriptor)
                    val dataBytes = gameViewModel.getGameState().serialize()
                        .toByteArray()  // Maybe I should serialize to bytes straight away...
                    fos.write(dataBytes)
                }
            }
            loadCode -> {
                if (uri != null) {
                    val contentResolver = applicationContext.contentResolver
                    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                    val fis = FileInputStream(parcelFileDescriptor?.fileDescriptor)
                    val byteArray = fis.readBytes()
                    val str = String(byteArray)
                    //TODO!+ Make de-serializing a GameState a static method...
                    val dummyGameState = GameState(mutableMapOf(),0)
                    val readGameState = dummyGameState.deserialize(str)
                    gameViewModel.setGameState(readGameState)

                    updateView()
                    showWinningBanner(false) //TODO?~ Shouldn't this be determined elsewhere...?
                }

            }
        }

    }

    private fun undo() {
        gameViewModel.undo()
        updateView()
    }

    private fun redo() {
        gameViewModel.redo()
        updateView()
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
        move(fun() = gameViewModel.right())
    }

    private fun left() {
        move(fun() = gameViewModel.left())
    }

    private fun up() {
        move(fun() = gameViewModel.up())
    }

    private fun down() {
        move(fun() = gameViewModel.down())
    }

    private fun move(moveInModel: () -> MoveResult) {
        val gameStateChanged = moveInModel()
        if (gameStateChanged.changeOccurred) {
            gameViewModel.placeNewValue()
        }

        updateView()
        if (gameStateChanged.highestNewValue >= 2048) {
            showWinningBanner(true)
        }

    }

    //TODO?~ Shouldn't this be called using the Observer mechanism, getting notified if the ViewModel's game state changes?
    private fun updateView() {
        gameBoardView.updateGameState(gameViewModel.getGameState())
        updateScoreDisplay()
    }

    private fun updateScoreDisplay() {
        val scoreText = getString(R.string.score, gameViewModel.getGameState().score)
        findViewById<TextView>(R.id.scoreView).text = scoreText
    }

    private fun showWinningBanner(visible: Boolean) {
        //TODO?~ Turn this into a nice animation.
        val winningBanner = findViewById<ImageView>(R.id.win2048)
        winningBanner.visibility = if (visible) { View.VISIBLE } else { View.INVISIBLE }
    }


}