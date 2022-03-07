package com.cormontia.android.game2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * View to display a 2048 board.
 */
class GameBoardView : View {

    private var gameBoard = mapOf<Coor,Int>()

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
    }

    fun updateGameState(gameState: Map<Coor,Int>) {
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

        val blackPaint = Paint()
        blackPaint.style = Paint.Style.STROKE
        blackPaint.strokeWidth = 4f
        blackPaint.textSize = 50f

        //TODO!= A test rectangle to see how the x and y coordinates are working.
        // It seems that the y coordinate is relative to the Constraint Layout, but the x coordinate is not?!?!?!
        val redPaint = Paint()
        redPaint.color = Color.RED
        redPaint.style = Paint.Style.FILL_AND_STROKE
        redPaint.strokeWidth = 4f
        val rect = Rect(0,0,210,210)
        canvas.drawRect(rect, redPaint)

        for (r in 1..4) {
            for (c in 1..4) {
                val x = paddingLeft + 100 * (c-1)
                val y = paddingTop + 100 * (r-1)
                val smallRect = Rect(x, y, x+90, y+90)
                canvas.drawRect(smallRect, blackPaint)

                val fieldValue = gameBoard[Coor(r,c)]
                if (fieldValue != null) {
                    //TODO!~ Adjust these offsets...
                    canvas.drawText(fieldValue.toString(), (x + 50).toFloat(), (y + 150).toFloat(), blackPaint)
                }
            }
        }
    }
}