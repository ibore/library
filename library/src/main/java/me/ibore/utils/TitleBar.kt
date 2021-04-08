package me.ibore.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import me.ibore.R
import me.ibore.utils.BarUtils
import me.ibore.utils.UIUtils

@Suppress("UNCHECKED_CAST")
class TitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(context, attrs, defStyleAttr) {

    var statusBar = true
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    private var shadowView: View? = null
    private var shadowHeight = 0
    private var shadowBackground: Drawable? = null
    private val statusBarHeight: Int
        get() = if (statusBar) BarUtils.getStatusBarHeight(context) else 0
    private var titleText: CharSequence? = null
    private var subTitleText: CharSequence? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0)
        statusBar = ta.getBoolean(R.styleable.TitleBar_tbStatusBar, statusBar)
        titleText = ta.getText(R.styleable.TitleBar_tbTitleText)
        subTitleText = ta.getText(R.styleable.TitleBar_tbSubTitleText)
        shadowHeight = ta.getDimensionPixelOffset(R.styleable.TitleBar_tbShadowHeight, shadowHeight)
        shadowBackground = ta.getDrawable(R.styleable.TitleBar_tbShadowBackground)
        ta.recycle()
    }

    companion object {
        const val NONE = 0
        const val TITLE = 1
        const val SUBTITLE = 2
        const val CENTER = 3
        const val START = 4
        const val END = 5
    }

    @IntDef(NONE, TITLE, SUBTITLE, CENTER, START, END)
    @Retention(AnnotationRetention.SOURCE)
    annotation class LayoutType

    private var titleView: View? = null
    private var subTitleView: View? = null
    private var centerView: View? = null
    private var startViews: MutableList<View> = ArrayList()
    private var endViews: MutableList<View> = ArrayList()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val layoutWidth = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) MeasureSpec.getSize(widthMeasureSpec) else 0
        val layoutHeight = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) MeasureSpec.getSize(heightMeasureSpec) else UIUtils.dp2px(context, 48F)
        startViews.clear()
        endViews.clear()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            when ((child.layoutParams as LayoutParams).titleType) {
                TITLE -> {
                    titleView = child
                    if (titleView is TextView && !titleText.isNullOrEmpty()) {
                        (titleView as TextView).text = titleText
                    }
                }
                SUBTITLE -> {
                    subTitleView = child
                    if (subTitleView is TextView && !subTitleText.isNullOrEmpty()) {
                        (subTitleView as TextView).text = subTitleText
                    }
                }
                CENTER -> centerView = child
                START -> startViews.add(child)
                END -> endViews.add(child)
            }
        }
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            when ((child.layoutParams as LayoutParams).titleType) {
                TITLE, SUBTITLE, CENTER -> {
                    measureChildWithMargins(child, MeasureSpec.makeMeasureSpec(layoutWidth - getStartWidth().coerceAtLeast(getEndWidth()) * 2 - paddingStart - paddingEnd, MeasureSpec.EXACTLY), 0,
                            MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY), 0)
                }
                else -> {
                    measureChildWithMargins(child, widthMeasureSpec, 0, MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY), 0)
                }
            }
        }
        setMeasuredDimension(layoutWidth, layoutHeight + statusBarHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        onLayoutStartViews(l, t + statusBarHeight - marginTop, r, b)
        onLayoutEndViews(l, t + statusBarHeight - marginTop, r, b)
        onLayoutCenterViews(l, t + statusBarHeight - marginTop, r, b)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if ((child.layoutParams as LayoutParams).titleType == NONE) {
                child.layout(l, t + statusBarHeight - marginTop, r, b)
            }
        }
    }

    private fun onLayoutCenterViews(l: Int, t: Int, r: Int, b: Int) {
        val start = l + paddingStart
        val top = t + paddingTop
        val end = r - paddingEnd
        val bottom = b - paddingBottom
        val startEndWidth = getStartWidth().coerceAtLeast(getEndWidth())
        centerView?.let {
            if (it.visibility == GONE) return@let
            val diffHeight = getDiffHeight(it, top, bottom)
            val diffWidth = getDiffWidth(it, start, end).coerceAtLeast(startEndWidth)
            it.layout(
                start + it.marginStart + diffWidth,
                top + it.marginTop + diffHeight,
                end - it.marginEnd - diffWidth,
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
            val diffWidth = getDiffWidth(it, start, end).coerceAtLeast(startEndWidth)
            it.layout(
                start + it.marginStart + diffWidth,
                top + it.marginTop + diffHeight,
                end - it.marginEnd - diffWidth,
                top + it.marginTop + diffHeight + it.measuredHeight
            )
        }
        subTitleView?.let {
            if (it.visibility == GONE) return@let
            val diffHeight = getDiffHeight(it, top, bottom) - titleViewHeight / 2
            val diffWidth = getDiffWidth(it, start, end).coerceAtLeast(startEndWidth)
            it.layout(
                start + it.marginStart + diffWidth,
                bottom - it.marginBottom - diffHeight - it.measuredHeight,
                end - it.marginEnd - diffWidth,
                bottom - it.marginBottom - diffHeight
            )
        }

    }

    private fun onLayoutStartViews(l: Int, t: Int, r: Int, b: Int) {
        var start = l + paddingStart
        val top = t + paddingTop
        val end = r - paddingEnd
        val bottom = b - paddingBottom
        for (i in 0 until startViews.size) {
            val child: View = startViews[i]
            if (child.visibility != GONE) {
                start += child.marginStart
                val tempEnd = start + child.measuredWidth + child.marginEnd
                val diffHeight = getDiffHeight(child, top, bottom)
                child.layout(
                    start,
                    top + child.marginTop + diffHeight,
                    if (tempEnd > end) end else tempEnd,
                    bottom - child.marginBottom - diffHeight
                )
                start = if (tempEnd > end) end else tempEnd
            }
        }
    }

    private fun onLayoutEndViews(l: Int, t: Int, r: Int, b: Int) {
        val start = l + paddingStart
        val top = t + paddingTop
        var end = r - paddingEnd
        val bottom = b - paddingBottom
        for (i in 0 until endViews.size) {
            val child: View = endViews[i]
            if (child.visibility != GONE) {
                end -= child.marginEnd
                val tempStart = end - child.measuredWidth + child.marginStart
                val diffHeight = getDiffHeight(child, top, bottom)
                child.layout(
                    if (tempStart < start) start else tempStart,
                    top + child.marginTop + diffHeight,
                    end,
                    bottom - child.marginBottom - diffHeight
                )
                end -= if (tempStart < start) start else tempStart
            }
        }
    }

    private fun getStartWidth(): Int {
        var startWidth = 0
        for (i in 0 until startViews.size) {
            if (startViews[i].visibility != GONE) {
                startWidth += startViews[i].marginStart + startViews[i].measuredWidth + startViews[i].marginEnd
            }
        }
        return startWidth
    }

    private fun getEndWidth(): Int {
        var endWidth = 0
        for (i in 0 until endViews.size) {
            if (endViews[i].visibility != GONE) {
                endWidth += endViews[i].marginStart + endViews[i].measuredWidth + endViews[i].marginEnd
            }
        }
        return endWidth
    }

    private fun getDiffHeight(view: View, top: Int, bottom: Int): Int {
        return if (view.layoutParams.height == MATCH_PARENT) 0 else (bottom - top - view.measuredHeight - view.marginTop - view.marginBottom) / 2
    }

    private fun getDiffWidth(view: View, start: Int, end: Int): Int {
        return if (view.layoutParams.width == MATCH_PARENT) 0 else (end - start - view.measuredWidth - view.marginStart - view.marginEnd) / 2
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
