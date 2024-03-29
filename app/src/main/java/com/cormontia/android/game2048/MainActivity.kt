package com.cormontia.android.game2048

import android.net.Uri
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
import com.cormontia.android.game2048.contracts.LoaderContract
import com.cormontia.android.game2048.contracts.SaverContract
import com.cormontia.android.game2048.contracts.SharerContract
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    companion object {
        const val storageFileMimeType = "text/plain"
        const val bitmapMimeType = "img/bmp"
        const val jpegMimeType = "image/jpeg"

        const val nrOfRows = 4
        const val nrOfColumns = 4
    }

    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameBoardView: GameBoardView
    private lateinit var mGestureDetector: GestureDetectorCompat

    private val loadLauncher = registerForActivityResult(LoaderContract()) { uri -> load(uri) }
    private val saveLauncher = registerForActivityResult(SaverContract()) { uri -> save(uri) }
    private val shareLauncher = registerForActivityResult(SharerContract()) { }

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
        findViewById<ImageButton>(R.id.saveButton).setOnClickListener { saveLauncher.launch("dummy") }
        findViewById<ImageView>(R.id.loadButton).setOnClickListener { loadLauncher.launch("dummy") }
        findViewById<ImageView>(R.id.shareButton).setOnClickListener {
            val uri = ImageMaker.createImage(this, gameBoardView)
            shareLauncher.launch(uri)
        }

        updateView()
    }

    private fun newGame() {
        gameViewModel.startNewGame()
        updateView()
        showWinningBanner(false) //TODO?~ Shouldn't this be in the "updateView" somehow...?
    }

    private fun load(uri: Uri?) {
        if (uri != null) {
            val contentResolver = applicationContext.contentResolver
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fis = FileInputStream(parcelFileDescriptor?.fileDescriptor)
            val byteArray = fis.readBytes()
            val str = String(byteArray)
            //TODO!+ Make de-serializing a GameState a static method...
            val dummyGameState = GameState(nrOfRows, nrOfColumns, mutableMapOf(),0)
            val readGameState = dummyGameState.deserialize(str)
            gameViewModel.setGameState(readGameState)
            parcelFileDescriptor?.close()

            updateView()
            showWinningBanner(false) //TODO?~ Shouldn't this be determined elsewhere...?
        }
    }

    private fun save(uri: Uri?) {
        if (uri != null) {
            //TODO!+ Handle FileNotFoundException and IOException
            val contentResolver = applicationContext.contentResolver
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w")
            val fos = FileOutputStream(parcelFileDescriptor?.fileDescriptor)
            val dataBytes = gameViewModel.getGameState().serialize()
                .toByteArray()  // Maybe I should serialize to bytes straight away...
            fos.write(dataBytes)

            parcelFileDescriptor?.close()
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
        //move(fun() = gameViewModel.right())
        move(fun() = gameViewModel.right())
    }

    private fun left() {
        //move(fun() = gameViewModel.left())
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
        } else  {
            val noMoreMoves = !gameViewModel.movesAvailable()
            showLosingBanner(noMoreMoves)
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

    //TODO!- We shouldn't use an IMAGE to show TEXT!
    // If nothing else, it makes translating unnecessarily complicated...
    private fun showLosingBanner(visible: Boolean) {
        val losingBanner = findViewById<ImageView>(R.id.losingBanner)
        losingBanner.visibility = if (visible) { View.VISIBLE } else { View.INVISIBLE }
    }


}