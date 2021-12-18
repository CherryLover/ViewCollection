package io.liuliu.uikits.widget.v3.game

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import me.monster.viewcollection.widget.dp

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/6 2:29 下午
 */
class GameOptionButtonWave @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val color_start = Color.parseColor("#C900FF")
        val color_ready = Color.parseColor("#F400A0")
    }

    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.5F.dp.toFloat()
        color = color_start
    }
    private val waveFreezePaint = Paint(wavePaint)

    private val freezeRect: RectF = RectF(0F, 0F, (83.dp * 1F), (38.dp * 1F))
    private val waveRect: RectF = RectF(1.dp.toFloat(), 1.dp.toFloat(), (96.dp * 1F), (51.dp * 1F))


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(98.dp, 53.dp)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateWaveOffset(w, h)

        val offset1X = (w - freezeRect.width()) / 2F
        val offset1Y = (h - freezeRect.height()) / 2F
        freezeRect.left = offset1X
        freezeRect.top = offset1Y
        freezeRect.right = freezeRect.right + offset1X
        freezeRect.bottom = freezeRect.bottom +offset1Y
    }

    private var calculateWaveOffset = false

    private fun calculateWaveOffset(w: Int, h: Int) {
        val offsetX = (w - waveRect.width()) / 2F
        val offsetY = (h - waveRect.height()) / 2F
        waveRect.left = offsetX
        waveRect.top = offsetY
        waveRect.right = waveRect.right + offsetX
        waveRect.bottom = waveRect.bottom + offsetY
        calculateWaveOffset = true
        waveSet = null
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(freezeRect, freezeRect.height() / 2, freezeRect.height() / 2, waveFreezePaint)
        if (waveSet?.isRunning == true) {
            canvas.drawRoundRect(waveRect, waveRect.height() / 2, waveRect.height() / 2, wavePaint)
        }
    }

    private var waveSet: AnimatorSet? = null

    fun changeColor(isReady: Boolean) {
        wavePaint.color = if (isReady) color_ready else color_start
        waveFreezePaint.color = wavePaint.color
    }

    private val postRunnable = Runnable { checkStartAnim() }

    /**
     * music app 项目在接入使用时，某些场景下，会出现先调用 waveAnim 然后 View 才会走到 onSizeChanged 的情况
     * 1. 检查是否已经经过 onSizeChanged；
     * 2. 每次 onSizeChanged 后主动更新动画区域；
     */
    fun waveAnim() {
        if (calculateWaveOffset) {
            checkStartAnim()
        } else {
            post(postRunnable)
        }
    }

    private fun checkStartAnim() {
        if (waveSet == null) {
            val alpha = ValueAnimator.ofInt(255, 0).apply {
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener { wavePaint.alpha = it.animatedValue as Int }
            }
            val left = ValueAnimator.ofFloat(freezeRect.left, waveRect.left).apply {
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener { waveRect.left = it.animatedValue as Float }
            }
            val top = ValueAnimator.ofFloat(freezeRect.top, waveRect.top).apply {
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener { waveRect.top = it.animatedValue as Float }
            }
            val right = ValueAnimator.ofFloat(freezeRect.right, waveRect.right).apply {
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener { waveRect.right = it.animatedValue as Float }
            }
            val bottom = ValueAnimator.ofFloat(freezeRect.bottom, waveRect.bottom).apply {
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener {
                    waveRect.bottom = it.animatedValue as Float
                    invalidate()
                }
            }

            waveSet = AnimatorSet().apply {
                playTogether(alpha, left, top, right, bottom)
                duration = 1000
            }
        }
        waveSet?.apply {
            if (!isRunning) {
                start()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(postRunnable)
    }

    fun stopAnim() {
        waveSet?.apply {
            if (isRunning) {
                end()
            }
        }
    }
}