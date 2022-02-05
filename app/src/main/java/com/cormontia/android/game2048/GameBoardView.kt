package com.cormontia.android.game2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val blackPaint = Paint(Color.BLACK)
        blackPaint.style = Paint.Style.STROKE
        blackPaint.strokeWidth = 4f
        blackPaint.textSize = 50f
        val rect = Rect(10,10,210,210)
        //canvas.drawRect(rect, blackPaint)
        for (r in 1..4) {
            for (c in 1..4) {
                val x = 150 + 200 * (c-1)
                val y = 200 + 200 * (r-1)
                val smallRect = Rect(x, y, x+190, y+190)
                canvas.drawRect(smallRect, blackPaint)

                val fieldValue = gameBoard[Coor(r,c)]
                if (fieldValue != null) {
                    canvas.drawText(fieldValue.toString(), (x + 50).toFloat(), (y + 150).toFloat(), blackPaint)
                }
            }
        }
    }
}