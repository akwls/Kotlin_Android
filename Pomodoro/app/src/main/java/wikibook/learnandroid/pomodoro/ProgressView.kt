package wikibook.learnandroid.pomodoro

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.jar.Attributes

class ProgressView : View {
    var srcResId: Int = 0
    var bgColor: Int
    val showBackgroudImage : Boolean

    var progress: Double = 0.0
    set(value) {
        field = value
        if(value >= 100.0) field = 100.0
        if(value <= 0.0) field = 0.0

        invalidate()
    }

    lateinit var backgroundBitmap: Bitmap
    lateinit var srcRect: Rect
    lateinit var destRect: Rect
    var backgroundPaint = Paint().apply {
        isAntiAlias = true
        alpha = 127
    }

    var backgroundCirclePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    var progressCirclePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        var array = context.obtainStyledAttributes(attrs, R.styleable.ProgressViewAttrs)

        bgColor = array.getColor(R.styleable.ProgressViewAttrs_progressBackgroundColor, 0)

        showBackgroudImage = array.getBoolean(R.styleable.ProgressViewAttrs_showBackgroundImage, false)

        srcResId = array.getResourceId(R.styleable.ProgressViewAttrs_progressBackgroundImage, 0)

        array.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(showBackgroudImage) {
            val bitmap = BitmapFactory.decodeResource(context.resources, srcResId)

            srcRect = Rect(0, 0, bitmap.width, bitmap.height)
            destRect = Rect(0, 0, w ,h)
            backgroundBitmap = bitmap
        }

        backgroundCirclePaint.color = Color.argb(25, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor))
        progressCirclePaint.color = Color.argb(127, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor))

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            if(showBackgroudImage) {
                drawBitmap(backgroundBitmap, srcRect, destRect, backgroundPaint)
            }

            val x = canvas.width / 2
            val y = canvas.height / 2
            val radius = canvas.width / 2
            drawCircle(x.toFloat(), y.toFloat(), radius.toFloat(), backgroundCirclePaint)
            drawCircle(x.toFloat(), y.toFloat(), (radius * (progress / 100)).toFloat(), progressCirclePaint)
        }
    }
}