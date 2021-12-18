package me.monster.viewcollection.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.animation.addListener
import me.monster.viewcollection.R

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/16 8:42 下午
 */
class SpaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "SpaceView"
    }

    private val minHeight: Float
    private val toMinLevel: Float
    val toMaxLevel: Float

    private val defaultHeight: Float

    var maxListener: (() -> Unit)? = null

    private var spaceHeight = 0

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SpaceView)
        minHeight = ta.getDimensionPixelSize(R.styleable.SpaceView_spaceMinHeight, 150.dp).toFloat()
        toMinLevel = ta.getDimensionPixelSize(R.styleable.SpaceView_spaceToMinLevel, 300.dp).toFloat()
        toMaxLevel = ta.getDimensionPixelSize(R.styleable.SpaceView_spaceToMaxLevel, 600.dp).toFloat()
        defaultHeight = ta.getDimensionPixelSize(R.styleable.SpaceView_spaceDefaultHeight, 450.dp).toFloat()
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 拖拽滑动，dy 为滑动距离，向上为负数
     */
    fun changeBounds(dy: Float) {
        val sy = 0 - dy
        Log.d(TAG, "receive dy scroll $sy")
        val attemptHeight = spaceHeight + sy.toInt()
        if (sy > 0) {
            // 向下滑动，Space 高度变大
        } else if (sy < 0) {
            // 向上滑动，Space 高度变小
            if (attemptHeight < minHeight) {
                // note: 单次滑动超过最小时，无法到达顶部，会停留在当前，更好的做法是滑动超过了距离时，仅滑到最大距离
                return
            }
            if (attemptHeight > minHeight && attemptHeight < toMinLevel) {
                // note: 松手后回到最小
            }
        }

        val tempParams = layoutParams
        tempParams.height = spaceHeight + sy.toInt()
        layoutParams = tempParams
    }

    /**
     * 用户松手
     */
    fun onPointUp() {
        val cHeight = getViewHeight()
        if (cHeight > defaultHeight) {
//            下拉
            if (cHeight >= toMaxLevel) {
                maxListener?.invoke()
            } else {
                changeLevelBounds(cHeight, defaultHeight.toInt())
            }
        } else {
//            上滑
            if (cHeight <= toMinLevel) {
                changeLevelBounds(cHeight, minHeight.toInt())
            } else {
                changeLevelBounds(cHeight, defaultHeight.toInt())
            }
        }
    }

    /**
     * 恢复至默认尺寸
     */
    private fun changeLevelBounds(cHeight: Int, h: Int) {
        ValueAnimator.ofInt(cHeight, h).apply {
            duration = 100
            addListener(onEnd = {
                removeAllUpdateListeners()
            }, onCancel = {
                removeAllUpdateListeners()
            })
            addUpdateListener {
                val tempParams = layoutParams
                tempParams.height = it.animatedValue as Int
                layoutParams = tempParams
            }
            start()
        }
    }

    /**
     * 外部更新 SpaceHeight 的高度
     */
    fun refreshSpaceHeight() {
        spaceHeight = getViewHeight()
    }

    private fun getViewHeight(): Int {
        return if (layoutParams.height < 0) {
            measuredHeight
        } else {
            layoutParams.height
        }
    }
}