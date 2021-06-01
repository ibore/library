package me.ibore.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewDebug.ExportedProperty
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.core.view.*
import me.ibore.R
import me.ibore.ktx.marginAndMeasureHeight
import me.ibore.ktx.marginAndMeasureWidth

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

    private var titleHeight = 0
    private var contentHeight = 0
    private var bottomHeight = 0

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

    override fun onMeasure(widthMS: Int, heightMS: Int) {
        bindView()
        val widthMode = MeasureSpec.getMode(widthMS)
        val heightMode = MeasureSpec.getMode(heightMS)
        val sizeWidth = MeasureSpec.getSize(widthMS)
        val sizeHeight = MeasureSpec.getSize(heightMS)
        var layoutWidth = paddingLeft + paddingEnd
        var layoutHeight = paddingTop + paddingBottom
        titleView?.let {
            if (it.visibility == View.GONE) return@let
            measureChildWithMargins(it, widthMS, 0, heightMS, 0)
            titleHeight = it.marginAndMeasureHeight
        }
        bottomView?.let {
            if (it.visibility == View.GONE) return@let
            measureChildWithMargins(it, widthMS, 0, heightMS, 0)
            bottomHeight = it.marginAndMeasureHeight
        }
        var contentWMS: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
        var contentHMS: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
        contentView?.let {
            val contentHeightMS =
                MeasureSpec.makeMeasureSpec(sizeHeight - layoutHeight, MeasureSpec.EXACTLY)
            measureChildWithMargins(it, widthMS, 0, contentHeightMS, titleHeight + bottomHeight)
            contentHeight = it.marginAndMeasureHeight
            contentWMS = MeasureSpec.makeMeasureSpec(it.marginAndMeasureWidth, MeasureSpec.EXACTLY)
            contentHMS = MeasureSpec.makeMeasureSpec(it.marginAndMeasureHeight, MeasureSpec.EXACTLY)
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
            val childLp = child.layoutParams as LayoutParams
            if (!child.isGone && childLp.rootType == NONE) {
                var childHeightUsed = 0
                if (childLp.noneTop == CONTENT) childHeightUsed += titleHeight
                if (childLp.noneBottom == CONTENT) childHeightUsed += bottomHeight
                measureChildWithMargins(child, widthMS, 0, heightMS, childHeightUsed)
            }
        }
        when {
            widthMode == MeasureSpec.EXACTLY -> layoutWidth = sizeWidth
            null != contentView -> layoutWidth = contentView!!.marginAndMeasureWidth
            else -> {
                for (i in 0 until count) {
                    val child: View = getChildAt(i)
                    val marginWidth = child.marginAndMeasureWidth
                    layoutWidth = if (marginWidth > layoutWidth) marginWidth else layoutWidth
                }
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            layoutHeight = sizeHeight
        } else {
            val tcbHeight = titleHeight + contentHeight + bottomHeight
            for (i in 0 until count) {
                val child: View = getChildAt(i)
                val rootType = (child.layoutParams as LayoutParams).rootType
                if (!child.isGone && rootType != TITLE && rootType != CONTENT && rootType != BOTTOM) {
                    val marginHeight = child.marginAndMeasureHeight
                    layoutHeight = if (marginHeight > layoutHeight) marginHeight else layoutHeight
                }
            }
            layoutHeight = if (tcbHeight > layoutHeight) tcbHeight else layoutHeight
        }
        setMeasuredDimension(layoutWidth, layoutHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            if (lp.rootType == NONE) {
                val t = if (lp.noneTop == CONTENT) top + titleHeight else top
                val b = if (lp.noneBottom == CONTENT) top + bottomHeight else top
                onChildLayout(child, left, t, right, b)
            }
        }
        onChildLayout(titleView, left, top, right, top + titleHeight)
        onChildLayout(bottomView, left, bottom - bottomHeight, right, bottom)
        onChildLayout(contentView, left, top + titleHeight, right, bottom - bottomHeight)
        onChildLayout(loadingView, left, top + titleHeight, right, bottom - bottomHeight)
        onChildLayout(emptyView, left, top + titleHeight, right, bottom - bottomHeight)
        onChildLayout(errorView, left, top + titleHeight, right, bottom - bottomHeight)
    }

    private fun onChildLayout(child: View?, left: Int, top: Int, right: Int, bottom: Int) {
        if (null == child || child.isGone) return
        val lp = child.layoutParams as LayoutParams
        val l = left + paddingLeft + lp.leftMargin
        val t = top + paddingTop + lp.topMargin
        val r = right - paddingRight - lp.rightMargin
        val b = bottom - paddingBottom - lp.bottomMargin
        child.layout(l, t, r, b)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
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
        companion object {
            const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
            const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
        }

        @RootType
        @ExportedProperty(category = "layout")
        var rootType: Int = NONE

        @ExportedProperty(category = "layout")
        var noneTop: Int = TITLE

        @ExportedProperty(category = "layout")
        var noneBottom: Int = BOTTOM

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