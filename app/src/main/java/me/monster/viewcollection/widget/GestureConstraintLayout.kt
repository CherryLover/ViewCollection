package me.monster.viewcollection.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/16 7:27 下午
 */
@SuppressLint("ClickableViewAccessibility")
class GestureConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    companion object {
        private const val TAG = "GestureConstraintLayout"
    }


    /**
     * 滑动反馈监听 负数向下滑动，正数向上滑动
     */
    var onScrollDetector: ((dy: Float) -> Unit)? = null

    /**
     * [isCancel] 表示是通过 Cancel 事件还是 Up 事件进行触发的
     */
    var onUpDetector: ((isCancel: Boolean) -> Unit)? = null

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        /**
         * 与 onTouchEvent 中的 down 事件处理逻辑一致即可
         */
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            onScrollDetector?.invoke(e1.y - e2.y)
            return true
        }
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    init {
        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                onUpDetector?.invoke(false)
            }
            if (event.action == MotionEvent.ACTION_CANCEL) {
                onUpDetector?.invoke(true)
            }
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }

        setOnGenericMotionListener { v, event ->
            return@setOnGenericMotionListener if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                gestureDetector.onGenericMotionEvent(event)
            } else {
                Log.e(TAG, "now in sdk version less than 23")
                false
            }
        }
    }
}