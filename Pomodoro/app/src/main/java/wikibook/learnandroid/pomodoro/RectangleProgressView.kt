package wikibook.learnandroid.pomodoro

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class RectangleProgressView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    lateinit var frontRect: Rect
    lateinit var backgroundRect: Rect
    var directionReverse = attributeSet.getAttributeBooleanValue(R.styleable.RectangleProgressViewAttrs_directionReverse, false)

    var progress: Double = 0.0
        set(value) {
            field = value
            if(value >= 100.0) field = 100.0
            if(value <= 0.0) field = 0.0

            invalidate()
        }

    var frontPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    var backgroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        backgroundRect = Rect(0, 0, this.width, this.height)
        frontRect = Rect(0, 0, w, h)

        backgroundPaint.color = Color.parseColor("#D3D3D3")
        frontPaint.color = Color.parseColor("#808080")

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            drawRect(0.0F, 0.0F, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
            if(directionReverse) {
                drawRect(0.0F, 0.0F, canvas.width - (canvas.width * (progress / 100)).toFloat(), canvas.height.toFloat(), frontPaint)
            }
            else {
                drawRect(0.0F, 0.0F, (canvas.width * (progress / 100)).toFloat(), canvas.height.toFloat(), frontPaint)
            }

        }
    }
}