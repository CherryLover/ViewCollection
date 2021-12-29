package me.monster.viewcollection.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import me.monster.viewcollection.R

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/25 11:16 上午
 */
class LightPathView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "LightPathView"

        //        val edge = 1.dp
        private const val edgeF = 0f
    }

    private val btOrigin = BitmapFactory.decodeResource(resources, R.mipmap.img_light_long)
    private val btLightTop: Bitmap = btOrigin
    private val btLightBottom: Bitmap
    private val btLightLeft: Bitmap
    private val btLightRight: Bitmap

    private val bounds = RectF()
    private val debugPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 1.dp.toFloat()
    }

    private val lightPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 反向绘制四个圆角
     */
    private val rectPath = Path()
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private var topBtOffset = 0f
    private var bottomBtOffset = 0f
    private var leftBtOffset = 0f
    private var rightBtOffset = 0f

    /**
     * 动画路径
     */
    var showDebugPath = true
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 圆角的反转
     */
    var showRoundCorner = true
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LightPathView)
        showDebugPath = ta.getBoolean(R.styleable.LightPathView_lpv_showDebugPath, false)
        rectPaint.color = ta.getColor(R.styleable.LightPathView_lpv_cornerColor, Color.WHITE)
        ta.recycle()

        btLightBottom = rotate(180f)
        btLightLeft = rotate(-90f)
        btLightRight = rotate(90f)

        setOnClickListener {
            pathAnimation()
        }
    }

    private val radiusLength = 17.dp.toFloat()
    private val rectRadius = 22.dp.toFloat()

    private var topOffset = 0f - btLightTop.width
        set(value) {
            field = value
            invalidate()
        }
    private var bottomOffset = 0f - btLightBottom.width
        set(value) {
            field = value
            invalidate()
        }
    private var leftOffset = 0f - btLightLeft.height
        set(value) {
            field = value
            invalidate()
        }
    private var rightOffset = 0f - btLightRight.height
        set(value) {
            field = value
            invalidate()
        }

    // 动画提前结束的 相对 offset
    private var lessBtRatioH = 0.8f
    private var lessBtRatioV = 0.6f
    // 动画提前开始的 相对 offset
    private var startLessBtRatio = 0.9f

    private var topAnim: ValueAnimator? = null
    private var bottomAnim: ValueAnimator? = null
    private var leftAnim: ValueAnimator? = null
    private var rightAnim: ValueAnimator? = null

    private fun rotate(degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.setRotate(degree)
        return Bitmap.createBitmap(btOrigin, 0, 0, btOrigin.width, btOrigin.height, matrix, true)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val rect = RectF(edgeF, edgeF, w.toFloat() - edgeF, h.toFloat() - edgeF)
        val roundPath = Path()
        roundPath.addRoundRect(rect, rectRadius, rectRadius, Path.Direction.CW)
        rectPath.addRect(rect, Path.Direction.CW)
        rectPath.op(roundPath, Path.Op.DIFFERENCE)
//        bounds.set(edgeF, edgeF, w.toFloat() - edgeF, h.toFloat() - edgeF)
        bounds.set(
            edgeF + radiusLength,
            edgeF + radiusLength,
            w.toFloat() - edgeF - radiusLength,
            h.toFloat() - edgeF - radiusLength
        )

        // 动画 offset
        topOffset = bounds.left - btLightTop.width.toFloat() * startLessBtRatio
        bottomOffset = bounds.right - btLightBottom.width.toFloat() * (1 - startLessBtRatio)
        leftOffset = bounds.bottom - btLightLeft.height.toFloat() * (1 - startLessBtRatio)
        rightOffset = bounds.top - btLightRight.height.toFloat() * startLessBtRatio

        // 贴边 offset
        topBtOffset = 0 - btLightTop.height / 2f
        bottomBtOffset = h - btLightBottom.height / 2f
        leftBtOffset = 0 - btLightLeft.width / 2f
        rightBtOffset = w - btLightRight.width / 2f
    }

    override fun onDraw(canvas: Canvas) {
        if (showDebugPath) {
            canvas.drawRect(bounds, debugPaint)
        }
        // top
        if (topAnim?.isRunning == true) {
            canvas.drawBitmap(btLightTop, topOffset, topBtOffset, lightPaint)
        }
        // bottom
        if (bottomAnim?.isRunning == true) {
            canvas.drawBitmap(btLightBottom, bottomOffset, bottomBtOffset, lightPaint)
        }
        // left
        if (leftAnim?.isRunning == true) {
            canvas.drawBitmap(btLightLeft, leftBtOffset, leftOffset, lightPaint)
        }
        // right
        if (rightAnim?.isRunning == true) {
            canvas.drawBitmap(btLightRight, rightBtOffset, rightOffset, lightPaint)
        }

        if (showRoundCorner) {
            canvas.drawPath(rectPath, rectPaint)
        }
    }

    fun pathAnimation(time: Long = 3000, easing: Interpolator = LinearInterpolator()) {
        topAnim = ValueAnimator.ofFloat(bounds.left - btLightTop.width.toFloat() * startLessBtRatio, bounds.right).apply {
            interpolator = easing
            duration = time

            addUpdateListener {
                topOffset = it.animatedValue as Float
                if (topOffset + btLightTop.width * lessBtRatioH > bounds.right) {
                    if (rightAnim?.isRunning != true) {
                        rightAnim?.start()
                    }
                }
            }
        }
        bottomAnim = ValueAnimator.ofFloat(bounds.right - btLightBottom.width.toFloat() * (1 - startLessBtRatio), bounds.left - btLightBottom.width.toFloat()).apply {
            interpolator = easing
            duration = time

            addUpdateListener {
                bottomOffset = it.animatedValue as Float
                if ((bottomOffset + (0 - btLightBottom.width * lessBtRatioH)) < (bounds.left - btLightBottom.width.toFloat())) {
                    if (leftAnim?.isRunning != true) {
                        leftAnim?.start()
                    }
                }
            }
        }
        leftAnim = ValueAnimator.ofFloat(bounds.bottom - btLightLeft.height.toFloat() * (1 - startLessBtRatio), bounds.top - btLightLeft.height.toFloat()).apply {
            interpolator = easing
            duration = time

            addUpdateListener {
                leftOffset = it.animatedValue as Float
                if (leftOffset < bounds.top - btLightLeft.height.toFloat() + btLightLeft.height * lessBtRatioV) {
                    if (topAnim?.isRunning != true) {
                        topAnim?.start()
                    }
                }
            }
        }
        rightAnim = ValueAnimator.ofFloat(bounds.top - btLightRight.height.toFloat() * startLessBtRatio, bounds.bottom).apply {
            interpolator = easing
            duration = time

            addUpdateListener {
                rightOffset = it.animatedValue as Float
                if (rightOffset + btLightRight.height * lessBtRatioV > bounds.bottom) {
                    if (bottomAnim?.isRunning != true) {
                        bottomAnim?.start()
                    }
                }
            }
        }
        topAnim?.start()
        bottomAnim?.start()
    }

    fun stopAnim() {
        leftAnim?.cancel()
        rightAnim?.cancel()
        topAnim?.cancel()
        bottomAnim?.cancel()
        invalidate()
    }
}