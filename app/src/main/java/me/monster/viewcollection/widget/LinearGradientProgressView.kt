package me.monster.viewcollection.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import me.monster.viewcollection.R

/**
 * @description 矩形 + 半圆的渐变色进度条
 * @author: Created jiangjiwei in 2021/12/10 10:22 上午
 */
class LinearGradientProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val FILL_COLOR_1 = Color.parseColor("#FC4A1A")
        private val FILL_COLOR_2 = Color.parseColor("#F7B733")
    }

    private val fillColor: LinearGradient by lazy { LinearGradient(0F, height / 2F, width.toFloat(), height / 2F, fillColor1, fillColor2, Shader.TileMode.CLAMP) }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    private var rightClip = getClipWidth()
        set(value) {
            field = value
            invalidate()
        }

    private var clipProgressAnim: ValueAnimator? = null

    var borderColor: Int
    var fillColor1: Int
    var fillColor2: Int
    private val arrowLeft: Boolean

    /**
     * 动画执行方向，默认为从直角边到圆角边
     */
    var animationDirectionAdd: Boolean = true

    private val badge = 1.dp
    private var rightRadius = 0F

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LinearGradientProgressView)
        borderColor = ta.getColor(R.styleable.LinearGradientProgressView_lgpv_border_color, Color.GREEN)
        fillColor1 = ta.getColor(R.styleable.LinearGradientProgressView_lgpv_fill_color_left, FILL_COLOR_1)
        fillColor2 = ta.getColor(R.styleable.LinearGradientProgressView_lgpv_fill_color_left, FILL_COLOR_2)
        arrowLeft = ta.getInt(R.styleable.LinearGradientProgressView_lgpv_arrow_direction, 1) == 0
        animationDirectionAdd = ta.getInt(R.styleable.LinearGradientProgressView_lgpv_animation_direction, 0) == 0
        ta.recycle()

        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 1F.dp.toFloat()
        borderPaint.color = Color.GREEN

        fillPaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        fillPaint.shader = fillColor
        rightRadius = (height - badge * 2) / 2F

        if (arrowLeft) {
            path.addRect(getBadgeLeft(), getBadgeTop(), getBadgeRight() - rightRadius, getBadgeBottom(), Path.Direction.CW)
            val circleCut = Path()
            circleCut.addCircle(getBadgeRight() - rightRadius, rightRadius + badge, rightRadius, Path.Direction.CW)
            path.op(circleCut, Path.Op.UNION)
        } else {
            path.addRect(getBadgeLeft() + rightRadius, getBadgeTop(), getBadgeRight(), getBadgeBottom(), Path.Direction.CW)
            val circleCut = Path()
            circleCut.addCircle(getBadgeLeft() + rightRadius, rightRadius + badge, rightRadius, Path.Direction.CW)
            path.op(circleCut, Path.Op.UNION)
        }
        rightClip = getClipWidth()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, borderPaint)
        canvas.save()
        if (arrowLeft) {
            canvas.clipRect(0F, 0F, rightClip, height.toFloat())
        } else {
            canvas.clipRect(width.toFloat() - rightClip, 0F, width.toFloat(), height.toFloat())
        }
        canvas.drawPath(path, fillPaint)
        canvas.restore()
    }

    fun startProgress(duration: Long = 5000) {
        if (clipProgressAnim == null) {
            clipProgressAnim = ValueAnimator.ofFloat(0F, 1F).apply {
                this.duration = duration
                this.interpolator = LinearInterpolator()
                addUpdateListener {
                    rightClip = width * it.animatedFraction
                }
            }
        }
        clipProgressAnim?.apply {
            if (!isRunning) {
                if (animationDirectionAdd) {
                    start()
                } else {
                    reverse()
                }
            }
        }
    }

    private fun getClipWidth(): Float {
        return if (arrowLeft) {
            if (animationDirectionAdd) {
                0F
            } else {
                width.toFloat()
            }
        } else {
            if (animationDirectionAdd) {
                badge.toFloat()
            } else {
                width.toFloat()
            }
        }
    }

    private fun getBadgeLeft(): Float {
        return if (arrowLeft) {
            0F
        } else {
            badge.toFloat()
        }
    }

    private fun getBadgeTop(): Float {
        return badge.toFloat()
    }

    private fun getBadgeRight(): Float {
        return width - badge.toFloat()
    }

    private fun getBadgeBottom(): Float {
        return height - badge.toFloat()
    }


}