package me.ibore.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.IntDef
import androidx.core.text.TextUtilsCompat
import androidx.core.view.*
import me.ibore.R
import me.ibore.ktx.dp2px
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

        startViews.clear()
        topViews.clear()
        endViews.clear()
        bottomViews.clear()
        startWidth = 0
        endWidth = 0
        topHeight = 0
        bottomHeight = 0

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            throw RuntimeException("不能设置成MATH_PARENT或者具体高度")
        }
        val parentHMS = MeasureSpec.makeMeasureSpec(barHeight, MeasureSpec.EXACTLY)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val titleType = (child.layoutParams as LayoutParams).titleType
            if (titleType == TITLE) {
                titleView = child
            } else if (titleType == SUBTITLE) {
                subTitleView = child
            } else if (titleType == CENTER) {
                centerView = child
            } else if (titleType == START) {
                startViews.add(child)
                measureChildWithMargins(child, widthMeasureSpec, 0, parentHMS, 0)
                startWidth += measureWidthMargin(child)
            } else if (titleType == TOP) {
                topViews.add(child)
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                topHeight += measureHeightMargin(child)
            } else if (titleType == END) {
                endViews.add(child)
                measureChildWithMargins(child, widthMeasureSpec, 0, parentHMS, 0)
                endWidth += measureWidthMargin(child)
            } else if (titleType == BOTTOM) {
                bottomViews.add(child)
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                bottomHeight += measureHeightMargin(child)
            } else if (titleType == NONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, parentHMS, 0)
            }
        }
        if (centerMiddle) {
            val startEndWidth = startWidth.coerceAtLeast(endWidth)
            startWidth = startEndWidth
            endWidth = startEndWidth
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

    private fun measureWidthMargin(child: View): Int {
        return if (child.visibility != GONE) {
            child.marginLeft + child.measuredWidth + child.marginRight
        } else 0
    }

    private fun measureHeightMargin(child: View): Int {
        return if (child.visibility != GONE) {
            child.marginTop + child.measuredHeight + child.marginBottom
        } else 0
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = paddingLeft
        var top = paddingTop + statusBarHeight
        val right = r - l - paddingRight
        var bottom = b - t - paddingBottom
        // 摆放None的view
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if ((child.layoutParams as LayoutParams).titleType == NONE
                && child.visibility != View.GONE
            ) {
                child.layout(left, top, right, bottom)
            }
        }
        onLayoutTopBottomViews(topViews, left, top, right, top + topHeight)
        onLayoutTopBottomViews(bottomViews, left, bottom - bottomHeight, right, bottom)
        top += topHeight
        bottom -= bottomHeight
        if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) ==
            ViewCompat.LAYOUT_DIRECTION_RTL
        ) {
            onLayoutLeftRightViews(endViews, true, left, top, right, bottom)
            onLayoutLeftRightViews(startViews, false, left, top, right, bottom)
        } else {
            onLayoutLeftRightViews(startViews, true, left, top, right, bottom)
            onLayoutLeftRightViews(endViews, false, left, top, right, bottom)
        }
        onLayoutCenterViews(left, top, right, bottom)

    }


    private fun onLayoutTopBottomViews(
        views: MutableList<View>, left: Int, top: Int, right: Int, bottom: Int
    ) {
        var temp = top
        for (child in views) {
            if (child.visibility != GONE) {
                temp += child.marginTop
                child.layout(
                    left + child.marginLeft, temp,
                    right - child.marginRight, temp + child.measuredHeight
                )
                temp += child.measuredHeight + child.marginBottom
            }
        }
    }


    private fun onLayoutCenterViews(left: Int, top: Int, right: Int, bottom: Int) {
        centerView?.let {
            if (it.visibility == GONE) return@let
            val diffHeight = getDiffHeight(it, top, bottom)
            val diffWidth = getDiffWidth(it, left, right, startWidth + endWidth)
            it.layout(
                left + startWidth + it.marginLeft + diffWidth,
                top + it.marginTop + diffHeight,
                right - endWidth - it.marginRight - diffWidth,
                bottom - it.marginBottom - diffHeight
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

    private fun onLayoutLeftRightViews(
        views: MutableList<View>, isLeft: Boolean, left: Int, top: Int, right: Int, bottom: Int
    ) {
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

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.TitleBar_Layout)
            titleType = a.getInt(R.styleable.TitleBar_Layout_layout_titleType, NONE)
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
