package me.monster.viewcollection.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/16 6:04 下午
 */
class BottomSheetBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attrs) {
    companion object {
        const val TAG = "UserScrollBehavior"
    }

    private var spaceHeight = 0
    private var dependencyView: View? = null

    private val childScrollDetector = object : Function1<Float, Unit> {
        override fun invoke(dy: Float) {
            dependencyView?.let {
                if (it is SpaceView) {
                    it.changeBounds(dy)
                }
            }
        }
    }
    private val childUpDetector = object: Function1<Boolean, Unit> {
        override fun invoke(isCancel: Boolean) {
            dependencyView?.let {
                if (it is SpaceView) {
                    it.onPointUp()
                }
            }
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        child.layout(0, spaceHeight, parent.width, parent.height)
        return true
    }

    /**
     * dependency 变动后会回调，spaceHeight 会更新
     */
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        dependencyView = dependency
        if (dependency.tag.toString() == "Space") {
            spaceHeight = getDependViewHeight(dependency)
        }
        if (dependency is SpaceView) {
            dependency.refreshSpaceHeight()
        }
        if (child is GestureConstraintLayout) {
            child.onScrollDetector = childScrollDetector
            child.onUpDetector = childUpDetector
            updateChildHeight(child, parent.height - spaceHeight)
            return true
        }
        return child is GestureConstraintLayout
    }

    private fun updateChildHeight(child: View, shLess: Int) {
        val tempParams = child.layoutParams
        tempParams.height = shLess
        child.layoutParams = tempParams
    }

    private fun getDependViewHeight(dependency: View): Int {
        return if (dependency.layoutParams.height < 0) {
            dependency.measuredHeight
        } else {
            dependency.layoutParams.height
        }
    }
}