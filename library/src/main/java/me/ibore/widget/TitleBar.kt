package me.ibore.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.IntDef
import androidx.core.view.*
import me.ibore.R
import me.ibore.ktx.*
import me.ibore.utils.BarUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

@Suppress("UNCHECKED_CAST")
class TitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        const val NONE = 0
        const val TITLE = 1
        const val SUBTITLE = 2
        const val CENTER = 3
        const val START = 4
        const val TOP = 5
        const val END = 6
        const val BOTTOM = 7
    }

    @IntDef(NONE, TITLE, SUBTITLE, CENTER, START, END)
    @Retention(AnnotationRetention.SOURCE)
    annotation class LayoutType

    var statusBar = true
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    var barHeight = dp2px(48F)
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    var centerMiddle = true
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    private val statusBarHeight: Int
        get() = if (statusBar) BarUtils.getStatusBarHeight(context) else 0

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0)
        statusBar = ta.getBoolean(R.styleable.TitleBar_tbStatusBar, statusBar)
        barHeight = ta.getDimensionPixelSize(R.styleable.TitleBar_tbHeight, barHeight)
        centerMiddle = ta.getBoolean(R.styleable.TitleBar_tbCenterMiddle, centerMiddle)
        ta.recycle()
    }

    private var titleView: View? = null
    private var subTitleView: View? = null
    private var centerView: View? = null
    private var startViews: MutableList<View> = ArrayList()
    private var topViews: MutableList<View> = ArrayList()
    private var endViews: MutableList<View> = ArrayList()
    private var bottomViews: MutableList<View> = ArrayList()
    private var startWidth: Int = 0
    private var endWidth: Int = 0
    private var topHeight: Int = 0
    private var bottomHeight: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        resetData()
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            throw RuntimeException("高度不能设置成MATH_PARENT或者具体值")
        }
        val parentHMS = MeasureSpec.makeMeasureSpec(barHeight, MeasureSpec.EXACTLY)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.isGone) continue
            val childLp = child.layoutParams as LayoutParams
            val titleBackPress = childLp.titleBackPress
            if (titleBackPress) {
                context.getActivity()?.apply { child.setOnClickListener { onBackPressed() } }
            }
            when (childLp.titleType) {
                TITLE -> titleView = child
                SUBTITLE -> subTitleView = child
                CENTER -> centerView = child
                START -> {
                    startViews.add(child)
                    measureChildWithMargins(child, widthMeasureSpec, 0, parentHMS, 0)
                    startWidth += child.marginAndMeasureWidth
                }
                TOP -> {
                    topViews.add(child)
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                    topHeight += child.marginAndMeasureHeight
                }
                END -> {
                    endViews.add(child)
                    measureChildWithMargins(child, widthMeasureSpec, 0, parentHMS, 0)
                    endWidth += child.marginAndMeasureWidth
                }
                BOTTOM -> {
                    bottomViews.add(child)
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                    bottomHeight += child.marginAndMeasureHeight
                }
                NONE -> {
                    measureChildWithMargins(child, widthMeasureSpec, 0, parentHMS, 0)
                }
            }
        }
        if (centerMiddle) {
            startWidth = startWidth.coerceAtLeast(endWidth)
            endWidth = startWidth.coerceAtLeast(endWidth)
        }
        val widthUsed = startWidth + endWidth
        if (null != titleView && !titleView!!.isGone) {
            measureChildWithMargins(titleView!!, widthMeasureSpec, widthUsed, parentHMS, 0)
        }
        if (null != subTitleView && !subTitleView!!.isGone) {
            measureChildWithMargins(subTitleView!!, widthMeasureSpec, widthUsed, parentHMS, 0)
        }
        if (null != centerView && !centerView!!.isGone) {
            measureChildWithMargins(centerView!!, widthMeasureSpec, widthUsed, parentHMS, 0)
        }
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measuredHeight = statusBarHeight + topHeight + barHeight + bottomHeight
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = paddingLeft
        var top = paddingTop + statusBarHeight
        val right = r - l - paddingRight
        var bottom = b - t - paddingBottom
        // 摆放None的view
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val titleType = (child.layoutParams as LayoutParams).titleType
            if (titleType == NONE && child.visibility != View.GONE) {
                child.layout(left, top, right, bottom)
            }
        }
        onTopBottomLayout(true, left, top, right, top + topHeight)
        onTopBottomLayout(false, left, bottom - bottomHeight, right, bottom)
        top += topHeight
        bottom -= bottomHeight
        onLeftRightLayout(true, left, top, right, bottom)
        onLeftRightLayout(false, left, top, right, bottom)
        onCenterLayout(left, top, right, bottom)
    }


    private fun onTopBottomLayout(isTop: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val views: MutableList<View> = if (isTop) topViews else bottomViews
        var temp = top
        for (child in views) {
            if (child.isGone) continue
            temp += child.marginTop
            val l = left + child.marginLeft
            val r = right - child.marginRight
            child.layout(l, temp, r, temp + child.measuredHeight)
            temp += child.measuredHeight + child.marginBottom
        }
    }

    private fun onCenterLayout(left: Int, top: Int, right: Int, bottom: Int) {
        centerView?.run {
            if (isGone) return
            val diffHeight = getDiffHeight(this, top, bottom)
            val diffWidth = getDiffWidth(this, left, right, startWidth + endWidth)
            layout(
                left + startWidth + marginLeft + diffWidth,
                top + marginTop + diffHeight,
                right - endWidth - marginRight - diffWidth,
                bottom - marginBottom - diffHeight
            )
        }
        var titleViewHeight = 0
        titleView?.let {
            if (it.visibility == GONE) return@let
            titleViewHeight = it.marginTop + it.measuredHeight + it.marginBottom
        }
        var subTitleViewHeight = 0
        subTitleView?.let {
            if (it.visibility == GONE) return@let
            subTitleViewHeight = it.marginTop + it.measuredHeight + it.marginBottom
        }

        titleView?.let {
            if (it.visibility == GONE) return@let
            val diffHeight = getDiffHeight(it, top, bottom) - subTitleViewHeight / 2
            val diffWidth = getDiffWidth(it, left, right, startWidth + endWidth)
            it.layout(
                left + startWidth + it.marginLeft + diffWidth,
                top + it.marginTop + diffHeight,
                right - endWidth - it.marginRight - diffWidth,
                top + it.marginTop + diffHeight + it.measuredHeight
            )
        }
        subTitleView?.let {
            if (it.visibility == GONE) return@let
            val diffHeight = getDiffHeight(it, top, bottom) - titleViewHeight / 2
            val diffWidth = getDiffWidth(it, left, right, startWidth + endWidth)
            it.layout(
                left + startWidth + it.marginLeft + diffWidth,
                bottom - it.marginBottom - diffHeight - it.measuredHeight,
                right - endWidth - it.marginRight - diffWidth,
                bottom - it.marginBottom - diffHeight
            )
        }
    }

    private fun onLeftRightLayout(isLeft: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val views =
            if ((isLeft && !isLayoutRtl) || (!isLeft && isLayoutRtl)) startViews else endViews
        var temp = if (isLeft) left else right
        for (i in 0 until views.size) {
            val child: View = views[i]
            if (child.visibility != GONE) {
                temp = if (isLeft) temp + child.marginLeft + child.measuredWidth
                else temp - child.marginRight
                val diffHeight = getDiffHeight(child, top, bottom)
                child.layout(
                    temp - child.measuredWidth, top + child.marginTop + diffHeight,
                    temp, bottom - child.marginBottom - diffHeight
                )
                temp = if (isLeft) temp + child.marginRight
                else temp - child.measuredWidth - child.marginLeft

            }
        }
    }

    private fun getDiffHeight(view: View, top: Int, bottom: Int): Int {
        return if (view.layoutParams.height == MATCH_PARENT) 0
        else abs((bottom - top - view.measuredHeight - view.marginTop - view.marginBottom) / 2)
    }

    private fun getDiffWidth(view: View, start: Int, end: Int, startEndWidth: Int): Int {
        return if (view.layoutParams.width == MATCH_PARENT) 0
        else abs((end - start - view.measuredWidth - view.marginLeft - view.marginRight - startEndWidth) / 2)
    }

    private fun resetData() {
        startViews.clear()
        topViews.clear()
        endViews.clear()
        bottomViews.clear()
        startWidth = 0
        endWidth = 0
        topHeight = 0
        bottomHeight = 0
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    open class LayoutParams : MarginLayoutParams {

        var titleType: Int = NONE
        var titleBackPress: Boolean = false

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.TitleBar_Layout)
            titleType = a.getInt(R.styleable.TitleBar_Layout_layout_titleType, NONE)
            titleBackPress =
                a.getBoolean(R.styleable.TitleBar_Layout_layout_titleBackPress, titleBackPress)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)

        constructor(width: Int, height: Int, @LayoutType layoutType: Int) : super(width, height) {
            this.titleType = layoutType
        }

        constructor(source: ViewGroup.LayoutParams) : super(source)

        constructor(source: MarginLayoutParams) : super(source)

        constructor(source: LayoutParams) : super(source) {
            this.titleType = source.titleType
        }

    }


}
