package com.cormontia.android.game2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

/**
 * View to display a 2048 board.
 */
class GameBoardView : View {

    private var gameBoard = GameState(HashMap())

    private val blackPaint = Paint()

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context,attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        blackPaint.style = Paint.Style.STROKE
        blackPaint.strokeWidth = 4f
        blackPaint.textSize = 40f
    }

    fun updateGameState(gameState: GameState) {
        this.gameBoard = gameState
        invalidate()    // Force repaint.
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //super.onSizeChanged(w, h, oldw, oldh)
        val xpad = paddingLeft + paddingRight
        val ypad = paddingTop + paddingBottom
        val widthForDrawing = w - xpad
        val heightForDrawing = h - ypad
        Log.i("2048-game", "widthForDrawing==$widthForDrawing, heightForDrawing=$heightForDrawing.")
    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        Log.i("2048-game", "paddingLeft==$paddingLeft.")

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        Log.i("2048-game", "width==$width, height==$height")

        // The width of our block is 400.
        // So it should start at (contentWidth / 2) - 200. Well, plus padding.
        // Analogous for the y offset.
        val x0 = contentWidth / 2 - 200
        val y0 = contentHeight / 2 - 200
        for (r in 1..4) {
            for (c in 1..4) {
                val x = paddingLeft + x0 + 100 * (c-1)
                val y = paddingTop + y0 + 100 * (r-1)
                val smallRect = Rect(x, y, x+90, y+90)
                canvas.drawRect(smallRect, blackPaint)

                val fieldValue = gameBoard.state[Coor(r,c)]
                if (fieldValue != null) {
                    //TODO!~ Adjust these offsets...
                    canvas.drawText(fieldValue.toString(), (x + 50).toFloat(), (y + 50).toFloat(), blackPaint)
                }
            }
        }
    }
}