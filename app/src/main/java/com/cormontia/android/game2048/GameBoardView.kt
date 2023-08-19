package com.cormontia.android.game2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.rgb
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * View to display a 2048 board.
 */
class GameBoardView : View, View.OnLayoutChangeListener {

    private var gameBoard = GameState(HashMap(), 0)

    // Paint for the text
    private val blackPaint = Paint()

    // Paints for the different background colors:
    var backgroundPaints : Map<Int, Paint> = mutableMapOf()

    // Window size variables.
    private var windowSizeVariablesNeedRecalc = true
    private var _paddingLeft = 0
    private var _paddingTop = 0
    private var _paddingRight = 0
    private var _paddingBottom = 0
    private var contentWidth = 0
    private var contentHeight = 0


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

        addOnLayoutChangeListener(this)
    }

    override fun onLayoutChange(
        view: View?,
        left: Int, top: Int, right: Int, bottom: Int,
        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int,
    ) {
        //TODO?~ Can we do the re-calculation here?
        windowSizeVariablesNeedRecalc = true
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //super.onSizeChanged(w, h, oldw, oldh)
        val xpad = paddingLeft + paddingRight
        val ypad = paddingTop + paddingBottom
        val widthForDrawing = w - xpad
        val heightForDrawing = h - ypad
        Log.i("2048-game", "widthForDrawing==$widthForDrawing, heightForDrawing=$heightForDrawing.")

        windowSizeVariablesNeedRecalc = true
    }

    override fun onDraw(canvas: Canvas) {

        //TODO?~ Can we use Lazy<..> for this?
        if (windowSizeVariablesNeedRecalc) {
            _paddingLeft = paddingLeft
            _paddingTop = paddingTop
            _paddingRight = paddingRight
            _paddingBottom = paddingBottom

            Log.i("2048-game", "paddingLeft==$_paddingLeft.")

            contentWidth = width - _paddingLeft - _paddingRight
            contentHeight = height - _paddingTop - _paddingBottom

            Log.i("2048-game", "width==$width, height==$height")

            windowSizeVariablesNeedRecalc = false
        }

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