package me.monster.viewcollection.widget

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import me.monster.viewcollection.R

/**
 * @description 引导卡片的自定义 View 绘制
 * @author: Created jiangjiwei in 2021/12/2 5:07 下午
 */
class GameGuideView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "GuideView"

        private const val content_alignment_start = 0
        private const val content_alignment_center = 1

        private const val arrow_diction_top = 0
        private const val arrow_diction_bottom = 1
    }

    private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val debugPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }

    private val borderColor: LinearGradient by lazy { LinearGradient(0F, 0F, width.toFloat(), height.toFloat(), borderColor1, borderColor2, Shader.TileMode.CLAMP) }
    private val fillColor: LinearGradient by lazy { LinearGradient(0F, 0F, width.toFloat(), height.toFloat(), fillColor1, fillColor2, Shader.TileMode.CLAMP) }

    private val path: Path = Path()
    private val triangle: Path = Path()

    private val edgeSpace = 2.dp.toFloat()
    private val cornerRadius: Float
    private val borderWidth: Float

    private val triangleBaseLength: Float
    private val triangleHeight: Float
    private val triangleTopRatio: Float
    var triangleBaseRatio: Float = 0.5F
        set(value) {
            field = value
            requestLayout()
        }
    private val triangleCenterInRect: Boolean

    private var title: String
    private var content: String

    private val textWrapWidth: Int

    private val titlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val contentPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var titleStatic: StaticLayout? = null
    private var contentStatic: StaticLayout ? = null

    private var titleHeight = 0F
    private var contentHeight = 0F

    private val textMarginV: Float
    private val textMarginH: Float

    private val arrowDirection: Int

    /**
     * 标题和内容的间隔
     */
    private val titleContentSpace: Float

    private val borderColor1: Int
    private val borderColor2: Int

    private val fillColor1: Int
    private val fillColor2: Int

    private var shakeVAnimator: ObjectAnimator? = null
    val alignment: Int
    val lineSpaceMultiplier: Float

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GameGuideView)
        borderWidth = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_border_width, 2.dp).toFloat()
        cornerRadius = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_corner_radius, 8.dp).toFloat()
        triangleBaseLength = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_arrow_base_length, 20.dp).toFloat()
        triangleHeight = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_arrow_height, 10.dp).toFloat()
        triangleTopRatio = ta.getFloat(R.styleable.GameGuideView_gv_arrow_top_ratio, 0.5F)
        triangleBaseRatio = ta.getFloat(R.styleable.GameGuideView_gv_arrow_base_length_ratio, 0.6F)
        triangleCenterInRect = ta.getBoolean(R.styleable.GameGuideView_gv_arrow_center_in_rect, true)
        title = ta.getString(R.styleable.GameGuideView_gv_title) ?: ""
        content = ta.getString(R.styleable.GameGuideView_gv_content) ?: ""
        textWrapWidth = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_text_wrap_width, 230.dp)
        alignment = ta.getInt(R.styleable.GameGuideView_gv_content_alignment, content_alignment_center)
        textMarginH = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_text_margin_h, 10.dp).toFloat()
        textMarginV = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_text_margin_v, 10.dp).toFloat()
        titleContentSpace = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_title_content_space, 10.dp).toFloat()
        arrowDirection = ta.getInt(R.styleable.GameGuideView_gv_arrow_diction, arrow_diction_top)
        borderColor1 = ta.getColor(R.styleable.GameGuideView_gv_border_color_1, Color.parseColor("#51F0FE"))
        borderColor2 = ta.getColor(R.styleable.GameGuideView_gv_border_color_2, Color.parseColor("#F603D6"))
        fillColor1 = ta.getColor(R.styleable.GameGuideView_gv_fill_color_2, Color.parseColor("#190380"))
        fillColor2 = ta.getColor(R.styleable.GameGuideView_gv_fill_color_2, Color.parseColor("#670DB7"))
        lineSpaceMultiplier = ta.getFloat(R.styleable.GameGuideView_gv_text_line_spacing_multiplier, 1.1F)
        val titleTextSize = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_title_text_size, 15.sp).toFloat()
        val contentTextSize = ta.getDimensionPixelSize(R.styleable.GameGuideView_gv_content_text_size, 12.sp).toFloat()
        ta.recycle()

        titlePaint.textSize = titleTextSize
        titlePaint.color = Color.WHITE
        titlePaint.typeface = Typeface.DEFAULT_BOLD
        contentPaint.textSize = contentTextSize
        contentPaint.color = Color.WHITE
        setupStaticLayout()
    }

    private fun getAlignment(): Layout.Alignment {
        return if (alignment == content_alignment_center) Layout.Alignment.ALIGN_CENTER else Layout.Alignment.ALIGN_NORMAL
    }

    private fun setupStaticLayout() {
        titleStatic = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(title, 0, title.length, TextPaint(titlePaint), textWrapWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0F, 1.1F)
                .build()
        } else {
            StaticLayout(title, TextPaint(titlePaint), textWrapWidth, Layout.Alignment.ALIGN_CENTER, 1.1F, 0F, true)
        }

        contentStatic = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(content, 0, content.length, TextPaint(contentPaint), textWrapWidth)
                .setAlignment(getAlignment())
                .setLineSpacing(0F, lineSpaceMultiplier)
                .build()
        } else {
            StaticLayout(content, TextPaint(contentPaint), textWrapWidth, getAlignment(), lineSpaceMultiplier, 0F, true)
        }
        titleHeight = if (title.isEmpty()) {
            0F
        } else {
            titleStatic!!.height.toFloat()
        }
        contentHeight = if (content.isEmpty()) {
            0F
        } else {
            contentStatic!!.height.toFloat()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val fW = textWrapWidth + (textMarginH * 2).toInt()
        val fH = (textMarginV * 2).toInt() + titleHeight.toInt() + (if (title.isEmpty()) 0 else titleContentSpace.toInt()) + contentHeight.toInt() + triangleHeight.toInt()
        setMeasuredDimension(fW, fH)
    }
//
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        setupPath(fW, fH)
        setupPath(w, h)

        bgPaint.style = Paint.Style.FILL
        bgPaint.shader = fillColor

        borderPaint.strokeCap = Paint.Cap.ROUND
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth
        borderPaint.shader = borderColor
    }

    private fun setupPath(width: Int, height: Int) {
        val attachLineY = if (arrowDirection == arrow_diction_bottom) {
            val rectBottom = height - edgeSpace - triangleHeight
            path.addRoundRect(
                edgeSpace,
                edgeSpace,
                width - edgeSpace,
                rectBottom,
                cornerRadius, cornerRadius, Path.Direction.CW
            )
            rectBottom
        } else {
            val rectTop = edgeSpace + triangleHeight
            path.addRoundRect(
                edgeSpace,
                rectTop,
                width - edgeSpace,
                height - edgeSpace,
                cornerRadius, cornerRadius, Path.Direction.CW
            )
            rectTop
        }

        val middle = (width - edgeSpace * 2) * 0.5F
        val halfBaseLength = triangleBaseLength * 0.5F
        // 三角形左顶点
        val ts = if (triangleCenterInRect) {
            middle - halfBaseLength
        } else {
            width * triangleBaseRatio
        }
        triangle.moveTo(ts, attachLineY)
        // 三角形右顶点
        triangle.lineTo(ts + triangleBaseLength, attachLineY)
        // 三角形上顶点
        if (arrowDirection == arrow_diction_bottom) {
            triangle.lineTo(ts + (triangleBaseLength * triangleTopRatio), attachLineY + triangleHeight)
        } else {
            triangle.lineTo(ts + (triangleBaseLength * triangleTopRatio), attachLineY - triangleHeight)
        }
        triangle.close()
        path.op(triangle, Path.Op.UNION)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, bgPaint)
        canvas.drawPath(path, borderPaint)

//        canvas.drawPath(triangle, debugPaint)
        val topDy = if (arrowDirection == arrow_diction_top) {
            textMarginV + triangleHeight
        } else {
            textMarginV
        }

        canvas.translate(textMarginH, topDy)
        titleStatic?.draw(canvas)
        val dy = if (title.isEmpty()) {
            titleHeight
        } else {
            titleHeight + titleContentSpace
        }
        canvas.translate(0F, dy)
        contentStatic?.draw(canvas)
    }

    fun alphaIn() {
        ObjectAnimator.ofFloat(this, "alpha", 0F, 1F).apply {
            duration = 200
            start()
        }
    }

    fun alphaOut() {
        ObjectAnimator.ofFloat(this, "alpha", 1F, 0F).apply {
            duration = 200
            start()
        }
    }

    fun rightIn() {
        ObjectAnimator.ofFloat(this, TRANSLATION_X, this.width.toFloat() * 2, 0F).apply {
            duration = 400
            start()
        }
    }

    fun rightOut() {
        ObjectAnimator.ofFloat(this, TRANSLATION_X, 0F, this.width.toFloat() * 2).apply {
            duration = 400
            start()
        }
    }

    /**
     * 垂直方向的抖动
     * [dy] 抖动距离
     * [loop] 是否循环
     */
    fun shakeVertical(
        loop: Boolean = false,
        dy: Float = 5.dp.toFloat(),
        duration: Long = 2000
    ) {
        if (shakeVAnimator == null) {
            val keyframeHolder = PropertyValuesHolder.ofKeyframe(
                TRANSLATION_Y,
                Keyframe.ofFloat(0F, 0F),
                Keyframe.ofFloat(0.25F, dy),
                Keyframe.ofFloat(0.50F, 0F),
                Keyframe.ofFloat(0.75F, -dy),
                Keyframe.ofFloat(1F, 0F)
            )
            shakeVAnimator = ObjectAnimator.ofPropertyValuesHolder(this, keyframeHolder).apply {
                this.duration = duration
                interpolator = LinearInterpolator()
                if (loop) {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.RESTART
                }
            }
        }
        if (shakeVAnimator?.isRunning == true) {
            return
        }
        shakeVAnimator?.start()
    }

    fun setupText(title: String, content: String) {
        this.title = title
        this.content = content
        setupStaticLayout()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        shakeVAnimator?.apply {
            if (isRunning) {
                end()
            }
        }
    }

}