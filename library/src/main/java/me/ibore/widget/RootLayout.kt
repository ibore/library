package me.ibore.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewDebug.ExportedProperty
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.core.view.isGone
import me.ibore.R
import me.ibore.ktx.marginAndMeasureHeight
import me.ibore.ktx.marginAndMeasureWidth

class RootLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {

        private const val DEFAULT_NONE_GRAVITY = Gravity.TOP or Gravity.START

        const val NONE = 0
        const val TITLE = 1
        const val CONTENT = 2
        const val BOTTOM = 3
        const val LOADING = 4
        const val EMPTY = 5
        const val ERROR = 6

        const val TITLE_BAR_TOP = 11
        const val TITLE_BAR_BOTTOM = 12
        const val BOTTOM_BAR_TOP = 13
        const val BOTTOM_BAR_BOTTOM = 14

    }

    @IntDef(NONE, TITLE, CONTENT, BOTTOM, LOADING, EMPTY, ERROR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class RootType

    @IntDef(CONTENT, LOADING, EMPTY, ERROR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ShowType

    @IntDef(TITLE_BAR_TOP, TITLE_BAR_BOTTOM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NoneTopOf

    @IntDef(BOTTOM_BAR_TOP, BOTTOM_BAR_BOTTOM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NoneBottomOf


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
    private var noneViews: MutableList<View> = ArrayList()

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
        noneViews.clear()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childLp = child.layoutParams as LayoutParams
            when (childLp.rootType) {
                TITLE -> {
                    if (titleView == child) continue
                    if (null != titleView) throw RuntimeException("title can only be one type of layout")
                    titleView = child
                }
                CONTENT -> {
                    if (contentView == child) continue
                    if (null != contentView) throw RuntimeException("content can only be one type of layout")
                    contentView = child
                }
                BOTTOM -> {
                    if (bottomView == child) continue
                    if (null != bottomView) throw RuntimeException("bottom can only be one type of layout")
                    bottomView = child
                }
                LOADING -> {
                    if (loadingView == child) continue
                    if (null != loadingView) throw RuntimeException("loading can only be one type of layout")
                    loadingView = child
                }
                EMPTY -> {
                    if (emptyView == child) continue
                    if (null != emptyView) throw RuntimeException("empty can only be one type of layout")
                    emptyView = child
                }
                ERROR -> {
                    if (errorView == child) continue
                    if (null != errorView) throw RuntimeException("error can only be one type of layout")
                    errorView = child
                }
                NONE -> {
                    noneViews.add(child)
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
        //
        onMeasureChild(titleView, widthMS, 0, heightMS, 0)
        titleHeight = titleView?.marginAndMeasureHeight ?: 0
        //
        onMeasureChild(bottomView, widthMS, 0, heightMS, 0)
        bottomHeight = bottomView?.marginAndMeasureHeight ?: 0
        //
        val heightUsed = paddingTop + paddingBottom + titleHeight + bottomHeight
        onMeasureChild(contentView, widthMS, 0, heightMS, heightUsed)
        val contentWidth = contentView?.marginAndMeasureWidth ?: 0
        contentHeight = contentView?.marginAndMeasureHeight ?: 0
        val contentWMS: Int = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY)
        val contentHMS: Int = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY)
        //
        onMeasureChild(loadingView, contentWMS, 0, contentHMS, 0)
        onMeasureChild(emptyView, contentWMS, 0, contentHMS, 0)
        onMeasureChild(errorView, contentWMS, 0, contentHMS, 0)
        for (child in noneViews) {
            val childLp = child.layoutParams as LayoutParams
            var childHeightUsed = 0
            if (childLp.noneTopOf == TITLE_BAR_BOTTOM) childHeightUsed += titleHeight
            if (childLp.noneBottomOf == BOTTOM_BAR_TOP) childHeightUsed += bottomHeight
            onMeasureChild(child, widthMS, 0, heightMS, childHeightUsed)
        }
        val layoutWidth = if (MeasureSpec.getMode(widthMS) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMS)
        } else {
            var tempWidth = 0
            for (i in 0 until childCount) {
                val child: View = getChildAt(i)
                val marginWidth = child.marginAndMeasureWidth
                tempWidth = if (marginWidth > tempWidth) marginWidth else tempWidth
            }
            tempWidth + paddingLeft + paddingEnd
        }
        val layoutHeight = if (MeasureSpec.getMode(heightMS) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMS)
        } else {
            var tempHeight = titleHeight + contentHeight + bottomHeight
            for (i in 0 until childCount) {
                val child: View = getChildAt(i)
                val rootType = (child.layoutParams as LayoutParams).rootType
                if (!child.isGone && rootType != TITLE && rootType != CONTENT && rootType != BOTTOM) {
                    val marginHeight = child.marginAndMeasureHeight
                    tempHeight = if (marginHeight > tempHeight) marginHeight else tempHeight
                }
            }
            tempHeight + paddingTop + paddingBottom
        }
        setMeasuredDimension(layoutWidth, layoutHeight)
    }

    fun onMeasureChild(child: View?, widthMS: Int, widthUsed: Int, heightMS: Int, heightUsed: Int) {
        if (null == child || child.isGone) return
        measureChildWithMargins(child, widthMS, widthUsed, heightMS, heightUsed)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = l + paddingLeft
        val top = t + paddingTop
        val right = r - l - paddingRight
        val bottom = b - t - paddingBottom
        for (child in noneViews) {
            val lp = child.layoutParams as LayoutParams
            var tempLeft = left
            var tempTop = top
            var tempRight = right
            var tempBottom = bottom
            if (lp.noneTopOf == TITLE_BAR_BOTTOM) {
                tempTop += titleHeight
            }
            if (lp.noneBottomOf == BOTTOM_BAR_TOP) {
                tempBottom -= bottomHeight
            }
            val absoluteGravity = Gravity.getAbsoluteGravity(lp.noneGravity, layoutDirection)
            val verticalGravity = lp.noneGravity and Gravity.VERTICAL_GRAVITY_MASK
            when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.LEFT -> tempRight = tempLeft + child.marginAndMeasureWidth
                Gravity.CENTER_HORIZONTAL -> {
                    tempLeft += (tempRight - tempLeft - child.marginAndMeasureWidth) / 2
                    tempRight = tempLeft + child.marginAndMeasureWidth
                }
                Gravity.RIGHT -> tempLeft = tempRight - child.marginAndMeasureWidth
                else -> tempRight = tempLeft + child.marginAndMeasureWidth
            }
            when (verticalGravity) {
                Gravity.TOP -> tempBottom = tempTop + child.marginAndMeasureHeight
                Gravity.CENTER_VERTICAL -> {
                    tempTop += (tempBottom - tempTop - child.marginAndMeasureHeight) / 2
                    tempBottom = tempTop + child.marginAndMeasureHeight
                }
                Gravity.BOTTOM -> tempTop = tempBottom - child.marginAndMeasureHeight
                else -> tempBottom = tempTop + child.marginAndMeasureHeight
            }
            onChildLayout(child, tempLeft, tempTop, tempRight, tempBottom)
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
        val l = left + lp.leftMargin
        val t = top + lp.topMargin
        val r = right - lp.leftMargin - lp.rightMargin
        val b = bottom - lp.topMargin - lp.bottomMargin
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

        @NoneTopOf
        @ExportedProperty(category = "layout")
        var noneTopOf: Int = TITLE_BAR_TOP

        @NoneBottomOf
        @ExportedProperty(category = "layout")
        var noneBottomOf: Int = BOTTOM_BAR_BOTTOM

        @ExportedProperty(category = "layout")
        var noneGravity: Int = DEFAULT_NONE_GRAVITY

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.RootLayout_Layout)
            rootType = a.getInt(R.styleable.RootLayout_Layout_layout_rootType, rootType)
            noneTopOf = a.getInt(R.styleable.RootLayout_Layout_layout_noneTopOf, noneTopOf)
            noneBottomOf = a.getInt(R.styleable.RootLayout_Layout_layout_noneBottomOf, noneBottomOf)
            noneGravity = a.getInt(R.styleable.RootLayout_Layout_layout_noneGravity, noneGravity)
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