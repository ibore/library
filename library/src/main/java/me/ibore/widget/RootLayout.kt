package me.ibore.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewDebug.ExportedProperty
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import me.ibore.R

class RootLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        const val NONE = 0
        const val TITLE = 1
        const val CONTENT = 2
        const val BOTTOM = 3
        const val LOADING = 4
        const val EMPTY = 5
        const val ERROR = 6
    }

    @IntDef(NONE, TITLE, CONTENT, BOTTOM, LOADING, EMPTY, ERROR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class RootType

    @IntDef(CONTENT, LOADING, EMPTY, ERROR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ShowType

    @ShowType
    var showType: Int = CONTENT
        private set(value) {
            field = value
            invalidate()
        }

    private var titleView: View? = null
    private var contentView: View? = null
    private var bottomView: View? = null
    private var loadingView: View? = null
    private var emptyView: View? = null
    private var errorView: View? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RootLayout, defStyleAttr, 0)
        showType = ta.getInt(R.styleable.RootLayout_rootShowType, CONTENT)
        ta.recycle()
    }

    fun hasLayoutType(@RootType layoutType: Int): Boolean {
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val pl = child.layoutParams as LayoutParams
            if (pl.rootType == layoutType) {
                return true
            }
        }
        return false
    }

    fun getChildView(@RootType layoutType: Int): View? {
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val pl = child.layoutParams as LayoutParams
            if (pl.rootType == layoutType) {
                return child
            }
        }
        return null
    }

    private fun bindView() {
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val childLp = child.layoutParams as LayoutParams
            when (childLp.rootType) {
                TITLE -> {
                    if (titleView == child) return
                    if (null != titleView) throw RuntimeException("title can only be one type of layout")
                    titleView = child
                }
                CONTENT -> {
                    if (contentView == child) return
                    if (null != contentView) throw RuntimeException("content can only be one type of layout")
                    contentView = child
                }
                BOTTOM -> {
                    if (bottomView == child) return
                    if (null != bottomView) throw RuntimeException("bottom can only be one type of layout")
                    bottomView = child
                }
                LOADING -> {
                    if (loadingView == child) return
                    if (null != loadingView) throw RuntimeException("loading can only be one type of layout")
                    loadingView = child
                }
                EMPTY -> {
                    if (emptyView == child) return
                    if (null != emptyView) throw RuntimeException("empty can only be one type of layout")
                    emptyView = child
                }
                ERROR -> {
                    if (errorView == child) return
                    if (null != errorView) throw RuntimeException("error can only be one type of layout")
                    errorView = child
                }
            }
        }
        contentView?.visibility = if (showType == CONTENT) VISIBLE else GONE
        loadingView?.visibility = if (showType == LOADING) VISIBLE else GONE
        emptyView?.visibility = if (showType == EMPTY) VISIBLE else GONE
        errorView?.visibility = if (showType == ERROR) VISIBLE else GONE

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        bindView()
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        var layoutWidth = paddingLeft + paddingEnd
        var layoutHeight = paddingTop + paddingBottom
        var heightUsed = 0

        titleView?.let {
            if (it.visibility == View.GONE) return@let
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
            heightUsed += it.measuredHeight + it.marginTop + it.marginBottom
        }
        bottomView?.let {
            if (it.visibility == View.GONE) return@let
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
            heightUsed += it.measuredHeight + it.marginTop + it.marginBottom
        }
        var contentWMS: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
        var contentHMS: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
        contentView?.let {
            val heightMS =
                MeasureSpec.makeMeasureSpec(sizeHeight - layoutHeight, MeasureSpec.EXACTLY)
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMS, heightUsed)
            contentWMS = MeasureSpec.makeMeasureSpec(it.measuredWidth, MeasureSpec.EXACTLY)
            contentHMS = MeasureSpec.makeMeasureSpec(it.measuredHeight, MeasureSpec.EXACTLY)
        }
        loadingView?.let {
            if (it.visibility == View.GONE) return@let
            measureChildWithMargins(it, contentWMS, 0, contentHMS, 0)
        }
        emptyView?.let {
            if (it.visibility == View.GONE) return@let
            measureChildWithMargins(it, contentWMS, 0, contentHMS, 0)
        }
        errorView?.let {
            if (it.visibility == View.GONE) return@let
            measureChildWithMargins(it, contentWMS, 0, contentHMS, 0)
        }
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE && (child.layoutParams as LayoutParams).rootType == NONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            }
        }
        var params: LayoutParams
        if (widthMode == MeasureSpec.EXACTLY) {
            //如果布局容器的宽度模式时确定的（具体的size或者match_parent）
            layoutWidth = sizeWidth
        } else {
            if (null != contentView) {
                val child: View = contentView!!
                params = child.layoutParams as LayoutParams
                layoutWidth = child.measuredWidth + params.leftMargin + params.rightMargin
            } else {
                for (i in 0 until count) {
                    val child: View = getChildAt(i)
                    params = child.layoutParams as LayoutParams
                    val marginWidth = child.measuredWidth + params.leftMargin + params.rightMargin
                    layoutWidth = if (marginWidth > layoutWidth) marginWidth else layoutWidth
                }
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            //如果布局容器的宽度模式时确定的（具体的size或者match_parent）
            layoutHeight = sizeHeight
        } else {
            var titleContentBottomHeight = 0
            for (i in 0 until count) {
                val child: View = getChildAt(i)
                if (child.visibility != GONE) {
                    params = child.layoutParams as LayoutParams
                    val marginHeight = child.measuredHeight + params.topMargin + params.bottomMargin
                    layoutHeight = if (marginHeight > layoutHeight) marginHeight else layoutHeight
                    if ((params.rootType == TITLE || params.rootType == CONTENT || params.rootType == BOTTOM)) {
                        titleContentBottomHeight += child.measuredHeight + params.topMargin + params.bottomMargin
                    }
                }
            }
            layoutHeight = if (titleContentBottomHeight > layoutHeight) titleContentBottomHeight else layoutHeight
        }
        // 测量并保存layout的宽高
        setMeasuredDimension(layoutWidth, layoutHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val tempLeft = left + paddingLeft
        var tempTop = top + paddingTop
        val tempRight = right - paddingRight
        var tempBottom = bottom - paddingBottom
        val count = childCount
        for (i in 0 until count) {
            val child: View = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            if (lp.rootType != TITLE && lp.rootType != CONTENT && lp.rootType != BOTTOM &&
                lp.rootType != LOADING && lp.rootType != EMPTY && lp.rootType != ERROR && child.visibility != GONE
            ) {
                child.layout(
                    tempLeft + lp.leftMargin,
                    tempTop + lp.topMargin,
                    tempRight - lp.rightMargin,
                    tempBottom - lp.bottomMargin
                )
            }
        }
        titleView?.let {
            if (it.visibility == View.GONE) return@let
            val lp = it.layoutParams as LayoutParams
            tempTop += lp.topMargin
            it.layout(
                tempLeft + lp.leftMargin,
                tempTop,
                tempRight - lp.rightMargin,
                tempTop + it.measuredHeight
            )
            tempTop += it.measuredHeight + lp.bottomMargin
        }
        bottomView?.let {
            if (it.visibility == View.GONE) return@let
            val lp = it.layoutParams as LayoutParams
            tempBottom -= lp.bottomMargin
            it.layout(
                tempLeft + lp.leftMargin,
                tempBottom - it.measuredHeight,
                tempRight - lp.rightMargin,
                tempBottom
            )
            tempBottom -= it.measuredHeight + lp.topMargin
        }
        contentView?.let {
            val lp = it.layoutParams as LayoutParams
            val childTop = tempTop + lp.topMargin
            val childBottom = tempBottom - lp.bottomMargin
            it.layout(tempLeft + lp.leftMargin, childTop, tempRight - lp.rightMargin, childBottom)
            loadingView?.layout(
                tempLeft + lp.leftMargin,
                childTop,
                tempRight - lp.rightMargin,
                childBottom
            )
            emptyView?.layout(tempLeft + lp.leftMargin, childTop, tempRight - lp.rightMargin, childBottom)
            errorView?.layout(tempLeft + lp.leftMargin, childTop, tempRight - lp.rightMargin, childBottom)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    fun showLoading() {
        showType = LOADING
    }

    fun showContent() {
        showType = CONTENT
    }

    fun showEmpty() {
        showType = EMPTY
    }

    fun showError() {
        showType = ERROR
    }

    open class LayoutParams : MarginLayoutParams {

        @RootType
        @ExportedProperty(category = "layout")
        var rootType: Int = NONE

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.RootLayout_Layout)
            rootType = a.getInt(R.styleable.RootLayout_Layout_layout_rootType, NONE)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)

        constructor(width: Int, height: Int, @RootType layoutType: Int) : super(width, height) {
            this.rootType = layoutType
        }

        constructor(source: ViewGroup.LayoutParams) : super(source)

        constructor(source: MarginLayoutParams) : super(source)

        constructor(source: LayoutParams) : super(source) {
            this.rootType = source.rootType
        }

    }
}