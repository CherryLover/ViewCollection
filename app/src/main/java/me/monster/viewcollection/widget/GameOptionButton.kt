package me.monster.viewcollection.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.addListener
import androidx.core.graphics.scale
import me.monster.viewcollection.R

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/4 1:50 下午
 */
internal class GameOptionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "GameOptionButton"
        private val base_width = 79.dp
        private val base_height = 34.dp
        private val border_width = 83.dp
        private val border_height = 38.dp
        private val startEn = R.mipmap.img_text_start_en
        private val matchEn = R.mipmap.img_text_match_en
        private val readyEn = R.mipmap.img_text_ready_en
        private val startId = R.mipmap.img_text_start_id
        private val matchId = R.mipmap.img_text_match_id
        private val readyId = R.mipmap.img_text_ready_id

        private val startColor1 = Color.parseColor("#F500BF")
        private val startColor2 = Color.parseColor("#8D00FF")
        private val matchColor1 = Color.parseColor("#FAC84A")
        private val matchColor2 = Color.parseColor("#ED3434")
        private val readyColor1 = Color.parseColor("#F600E2")
        private val readyColor2 = Color.parseColor("#E0005F")

        private val borderStartColor1 = Color.parseColor("#A6078E")
        private val borderStartColor2 = Color.parseColor("#6006B0")
        private val borderMatchColor1 = Color.parseColor("#A37D41")
        private val borderMatchColor2 = Color.parseColor("#982732")
        private val borderReadyColor1 = Color.parseColor("#F600E2")
        private val borderReadyColor2 = Color.parseColor("#E0005F")

        internal const val start = 0
        internal const val match = 1
        internal const val ready = 2
    }

    var showWave: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    private var textBp: Bitmap

    private var baseWidth = base_width
    private var baseHeight = base_height
    private var borderWidth = border_width
    private var borderHeight = border_height

    private var baseOffsetX = 0F
    private var baseOffsetY = 0F
    private var borderOffsetX = 0F
    private var borderOffsetY = 0F
    private var textOffsetX = 0F
    private var textOffsetY = 0F

    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 2.dp.toFloat()
        alpha = 125
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    private val baseColor: LinearGradient by lazy {
        val x = baseOffsetX + baseWidth / 2F
        checkColor(true, x)
    }

    private val borderColor: LinearGradient by lazy {
        val x = borderOffsetX + borderWidth / 2F
        checkColor(false, x)
    }
    private var v2xySet: AnimatorSet? = null
    private var v2xyReverseSet: AnimatorSet? = null
    internal var cType: Int = 0
        set(value) {
            field = value
            checkBitmap()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GameOptionButton)
        showWave = ta.getBoolean(R.styleable.GameOptionButton_gob_show_wave, true)
        cType = ta.getInt(R.styleable.GameOptionButton_gob_opt_type, start)
        ta.recycle()
        textBp = BitmapFactory.decodeResource(resources, startEn)
        checkBitmap()
    }

    private fun checkColor(isBase: Boolean, x: Float): LinearGradient {
        return if (isBase) {
            when (cType) {
                start -> LinearGradient(x, baseOffsetY, x, baseOffsetY + baseHeight, startColor1, startColor2, Shader.TileMode.CLAMP)
                match -> LinearGradient(x, baseOffsetY, x, baseOffsetY + baseHeight, matchColor1, matchColor2, Shader.TileMode.CLAMP)
                ready -> LinearGradient(x, baseOffsetY, x, baseOffsetY + baseHeight, readyColor1, readyColor2, Shader.TileMode.CLAMP)
                else -> LinearGradient(x, baseOffsetY, x, baseOffsetY + baseHeight, startColor1, startColor2, Shader.TileMode.CLAMP)
            }
        } else {
            when (cType) {
                start -> LinearGradient(x, baseOffsetY, x, baseOffsetY + baseHeight, borderStartColor1, borderStartColor2, Shader.TileMode.CLAMP)
                match -> LinearGradient(x, baseOffsetY, x, baseOffsetY + baseHeight, borderMatchColor1, borderMatchColor2, Shader.TileMode.CLAMP)
                ready -> LinearGradient(x, baseOffsetY, x, baseOffsetY + baseHeight, borderReadyColor1, borderReadyColor2, Shader.TileMode.CLAMP)
                else -> LinearGradient(x, baseOffsetY, x, baseOffsetY + baseHeight, borderStartColor1, borderStartColor2, Shader.TileMode.CLAMP)
            }
        }

    }

    fun changeLan(isEn: Boolean) {
        checkBitmap(isEn)
        invalidate()
    }

    private fun checkBitmap(isEn: Boolean = false) {
        val resId = if (isEn) {
            when (cType) {
                start -> startEn
                match -> matchEn
                ready -> readyEn
                else -> startEn
            }
        } else {
            when (cType) {
                start -> startId
                match -> matchId
                ready -> readyId
                else -> startId
            }
        }
        textBp = BitmapFactory.decodeResource(resources, resId)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        baseWidth = base_width
        baseHeight = base_height
        borderWidth = border_width
        borderHeight = border_height
        setMeasuredDimension(borderWidth, borderHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // baseOffset 不需要 / 2 因为比 border 要小
        baseOffsetX = (w - baseWidth).toFloat()
        baseOffsetY = (h - baseHeight).toFloat()
        if (showWave) {
            borderOffsetX = (w - borderWidth) / 2F
            borderOffsetY = (h - borderHeight) / 2F
            textOffsetX = borderOffsetX + (borderWidth - textBp.width).toFloat() / 2F
            textOffsetY = borderOffsetY + (borderHeight - textBp.height).toFloat() / 2F
        } else {
            borderOffsetX = (w - borderWidth) / 2F
            borderOffsetY = (h - borderHeight) / 2F
            if (textBp.height > borderHeight || textBp.width > borderWidth) {
                textOffsetX = borderOffsetX
                textOffsetY = borderOffsetY
                textBp = textBp.scale(borderWidth, borderHeight)
            } else {
                textOffsetX = (borderWidth - textBp.width).toFloat() / 2F
                textOffsetY = (borderHeight - textBp.height).toFloat() / 2F
            }
        }

        basePaint.shader = baseColor
        borderPaint.shader = borderColor
    }

    override fun onDraw(canvas: Canvas) {
        if (showWave) {
            // 填充
            canvas.drawRoundRect(borderOffsetX, borderOffsetY, borderOffsetX + borderWidth, borderOffsetY + borderHeight, borderHeight / 2F, borderHeight / 2F, basePaint)
            canvas.drawBitmap(textBp, textOffsetX, textOffsetY, textPaint)
        } else {
            // 边框
            canvas.drawRoundRect(borderOffsetX, borderOffsetY, borderWidth.toFloat(), borderHeight.toFloat(), borderHeight / 2F, borderHeight / 2F, borderPaint)
            // 填充
            canvas.drawRoundRect(baseOffsetX, baseOffsetY, baseWidth.toFloat(), baseHeight.toFloat(), baseHeight / 2F, baseHeight / 2F, basePaint)
            canvas.drawBitmap(textBp, textOffsetX, textOffsetY, textPaint)
        }
    }

    fun breathStartAnimV2() {
        if (v2xySet == null) {
            val x = ObjectAnimator.ofFloat(this, "scaleX", 1.0F, 0.9F)
            val y = ObjectAnimator.ofFloat(this, "scaleY", 1.0F, 0.9F).apply {
                addListener(onEnd = {
                    breathReverseAnimV2()
                })
            }
            v2xySet = AnimatorSet().apply {
                playTogether(x, y)
                duration = 500
            }
        }
        v2xySet?.let {
            if (!it.isRunning) {
                it.start()
            }
        }
    }

    private fun breathReverseAnimV2() {
        if (v2xyReverseSet == null) {
            val x = ObjectAnimator.ofFloat(this, "scaleX", 0.9F, 1.0F)
            val y = ObjectAnimator.ofFloat(this, "scaleY", 0.9F, 1.0F).apply {
                addListener(onEnd = {
                    breathStartAnimV2()
                })
            }
            v2xyReverseSet = AnimatorSet().apply {
                playTogether(x, y)
                duration = 500
            }
        }
        v2xyReverseSet?.let {
            if (!it.isRunning) {
                it.start()
            }
        }
    }

    fun stopAnimation() {
        v2xySet?.let {
            it.childAnimations.forEach {itemAni ->
                if (itemAni.isRunning) {
                    itemAni.end()
                }
            }
        }
        v2xyReverseSet?.let {
            it.childAnimations.forEach {itemAni ->
                if (itemAni.isRunning) {
                    itemAni.end()
                }
            }
        }
    }

}