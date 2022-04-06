package com.cormontia.android.game2048

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color.rgb
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

    private var gameBoard = GameState(HashMap(), 0)

    // Paint for the text
    private val blackPaint = Paint()

    // Paints for the different background colors:
    var backgroundPaints : Map<Int, Paint> = mutableMapOf()

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
        blackPaint.textSize = 30f

        val backgroundColors = mapOf(
              2  to rgb(255, 255,   0),
              4  to rgb(255, 192,   0),
              8  to rgb(255, 128,   0),
             16  to rgb(  0, 192, 255),
             32  to rgb(  0, 128, 255),
             64  to rgb(  0,  64, 255),
             128 to rgb(  0,   0, 255),
             256 to rgb(  0,  64,   0),
             512 to rgb(  0, 128,   0),
            1024 to rgb(  0, 192,   0),
            2048 to rgb(  0, 255,   0)
        )

        backgroundPaints = backgroundColors
            .map { (key, color) -> key to makePaintWithColor(color) }
            .toMap()
    }

    private fun makePaintWithColor(color: Int) : Paint {
        val paint = Paint()
        paint.color = color
        return paint
    }

    fun updateGameState(gameState: GameState) {
        this.gameBoard = gameState
        invalidate()    // Force repaint.
    }

    //TODO!- Just testing if I can send an Intent to the MainActivity...
    private fun sendUpIntent() {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("direction", Direction.UP)
        context.sendBroadcast(intent)
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

                val fieldValue = gameBoard.state[Coor(r,c)]
                if (fieldValue != null) {

                    val backgroundPaint = backgroundPaints[fieldValue]
                    if (backgroundPaint != null) {
                        canvas.drawRect(smallRect, backgroundPaint)
                    }

                    val xOffset = if (fieldValue > 1000)
                        10
                    else if (fieldValue > 100)
                        25
                    else if (fieldValue > 10)
                        40
                    else
                        50
                    canvas.drawText(fieldValue.toString(), (x + xOffset).toFloat(), (y + 50).toFloat(), blackPaint)
                }

                canvas.drawRect(smallRect, blackPaint)
            }
        }
    }
}