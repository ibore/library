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
        const val END = 5
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

    private val statusBarHeight: Int
        get() = if (statusBar) BarUtils.getStatusBarHeight(context) else 0

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0)
        statusBar = ta.getBoolean(R.styleable.TitleBar_tbStatusBar, statusBar)
        ta.recycle()
    }


    private var titleView: View? = null
    private var subTitleView: View? = null
    private var centerView: View? = null
    private var startViews: MutableList<View> = ArrayList()
    private var endViews: MutableList<View> = ArrayList()
    private var startEndWidth: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getMeasureSize(widthMeasureSpec, 0)
        val height = getMeasureSize(heightMeasureSpec, dp2px(48F))
        startViews.clear()
        endViews.clear()
        var startWidth = 0
        var endWidth = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            when ((child.layoutParams as LayoutParams).titleType) {
                TITLE -> titleView = child
                SUBTITLE -> subTitleView = child
                CENTER -> centerView = child
                START -> {
                    startViews.add(child)
                    measureChildWithMargins(
                        child, widthMeasureSpec, 0,
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), 0
                    )
                    startWidth += measureStartEndWidth(child)
                }
                END -> {
                    endViews.add(child)
                    measureChildWithMargins(
                        child, widthMeasureSpec, 0,
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), 0
                    )
                    endWidth += measureStartEndWidth(child)
                }
                NONE -> {
                    measureChildWithMargins(
                        child, widthMeasureSpec, 0,
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), 0
                    )
                }
            }
        }
        startEndWidth = startWidth.coerceAtLeast(endWidth)
        measureCenterView(titleView, width, height)
        measureCenterView(subTitleView, width, height)
        measureCenterView(centerView, width, height)
        setMeasuredDimension(width, height + statusBarHeight)
    }

    private fun measureStartEndWidth(child: View): Int {
        return if (child.visibility != GONE) {
            child.marginLeft + child.measuredWidth + child.marginRight
        } else 0
    }

    private fun measureCenterView(view: View?, width: Int, height: Int) {
        view?.let {
            measureChildWithMargins(
                it, MeasureSpec.makeMeasureSpec(width - startEndWidth * 2, MeasureSpec.EXACTLY),
                0, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), 0
            )
        }
    }

    private fun getMeasureSize(measureSpec: Int, defaultSize: Int): Int {
        return if (MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY)
            MeasureSpec.getSize(measureSpec) else defaultSize
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = paddingLeft
        val top = statusBarHeight + paddingTop
        val right = r - l - paddingRight
        val bottom = b - t - paddingBottom
        if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            onLayoutLeftRightViews(endViews, true, left, top, right, bottom)
            onLayoutLeftRightViews(startViews, false, left, top, right, bottom)
        } else {
            onLayoutLeftRightViews(startViews, true, left, top, right, bottom)
            onLayoutLeftRightViews(endViews, false, left, top, right, bottom)
        }
        onLayoutCenterViews(left, top, right, bottom)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if ((child.layoutParams as LayoutParams).titleType == NONE
                && child.visibility != View.GONE
            ) {
                child.layout(left, top, right, bottom)
            }
        }
    }

    private fun onLayoutCenterViews(left: Int, top: Int, right: Int, bottom: Int) {
        centerView?.let {
            if (it.visibility == GONE) return@let
            val diffHeight = getDiffHeight(it, top, bottom)
            val diffWidth = getDiffWidth(it, left, right, startEndWidth * 2)
            it.layout(
                left + startEndWidth + it.marginLeft + diffWidth,
                top + it.marginTop + diffHeight,
                right - startEndWidth - it.marginRight - diffWidth,
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
            val diffWidth = getDiffWidth(it, left, right, startEndWidth * 2)
            it.layout(
                left + startEndWidth + it.marginLeft + diffWidth,
                top + it.marginTop + diffHeight,
                right - startEndWidth - it.marginRight - diffWidth,
                top + it.marginTop + diffHeight + it.measuredHeight
            )
        }
        subTitleView?.let {
            if (it.visibility == GONE) return@let
            val diffHeight = getDiffHeight(it, top, bottom) - titleViewHeight / 2
            val diffWidth = getDiffWidth(it, left, right, startEndWidth * 2)
            it.layout(
                left + startEndWidth + it.marginLeft + diffWidth,
                bottom - it.marginBottom - diffHeight - it.measuredHeight,
                right - startEndWidth - it.marginRight - diffWidth,
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
