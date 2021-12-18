package me.monster.viewcollection.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import io.liuliu.uikits.widget.v3.game.GameOptionButtonWave
import me.monster.viewcollection.R

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/6 4:27 下午
 */
class GameOptionButtonLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        const val start = 0
        const val match = 1
        const val ready = 2
    }

    private val button = GameOptionButton(context).apply {
        val temp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        temp.gravity = Gravity.CENTER
        addView(this, temp)
    }
    private val wave = GameOptionButtonWave(context).apply {
        val temp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        temp.gravity = Gravity.CENTER
        addView(this, temp)
    }

    var showWave: Boolean = false
        set(value) {
            field = value
            button.showWave = field
            changeWaveView()
        }
    var cType: Int = GameOptionButton.start
        set(value) {
            field = value
            wave.changeColor(value == ready)
        }

    var lanIsEn = false
        set(value) {
            field = value
            button.changeLan(value)
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GameOptionButtonLayout)
        cType = ta.getInt(R.styleable.GameOptionButtonLayout_gobl_opt_type, start)
        showWave = ta.getBoolean(R.styleable.GameOptionButtonLayout_gobl_show_wave, false)
        ta.recycle()

        button.cType = cType
        button.showWave = showWave

        changeWaveView()
    }

    fun showAnimation() {
        button.breathStartAnimV2()
        wave.waveAnim()
    }

    fun stopAnimation() {
        wave.stopAnim()
        button.stopAnimation()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
    }

    private fun changeWaveView() {
        if (showWave) {
            wave.visibility = VISIBLE
        } else {
            wave.visibility = INVISIBLE
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isClickable) {
            return true
        }
        super.onTouchEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> beginScale(R.anim.set_scale_in)
            MotionEvent.ACTION_UP -> beginScale(R.anim.set_scale_out)
            MotionEvent.ACTION_CANCEL -> beginScale(R.anim.set_scale_out)
        }
        return true
    }

    override fun setVisibility(visibility: Int) {
        clearAnimation()
        super.setVisibility(visibility)
    }

    private fun beginScale(animation: Int) {
        if (!showWave) {
            val an = AnimationUtils.loadAnimation(context, animation)
            an.duration = 80
            an.fillAfter = true
            startAnimation(an)
        }
    }
}