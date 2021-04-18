package me.ibore.widget.refresh

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.OverScroller
import android.widget.ScrollView
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import me.ibore.R
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class RefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr), NestedScrollingParent {
    private val LOG_TAG = "RefreshLayout"

    private val MAX_OFFSET = 30                             //单次最大偏移量
    private var mHeader: RefreshHeader? = null
    private var mContentView: View? = null
    private var mHeaderView: View? = null

    private val mScroller by lazy { OverScroller(context) }
    private val mOffsetAnimator by lazy {
        ValueAnimator().apply {
            addUpdateListener {
                val value = animatedValue as Int
                moveView(value - mCurrentOffset)
            }
        }
    }
    private val mGesture: GestureDetectorCompat by lazy {
        GestureDetectorCompat(context, RefreshGestureListener()).apply { setIsLongpressEnabled(false) }
    }

    private var mLastFlingY: Int = 0
    private var mCurrentOffset: Int = 0
    private var mInitialDownY: Float = 0f

    /*状态参数↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
    private var mIsReset = true                             //刷新完成后是否重置
    private var mIsFling = false                            //是否处于Fling状态
    private var mRefreshing = false                         //是否正在刷新中
    private var mIsBeingDragged = false                     //是否开始拖动
    private var mGestureExecute = false                    //Gesture事件是否响应
    private var mNestedScrollExecute = false                //NestedScroll事件是否响应
    private var mNestedScrollInProgress = false             //NestedScroll是否可以执行，遇到部分手机不会走NestedScroll流程
    /*状态参数↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/

    /*可配置参数↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
    var defaultRefreshHeight: Int = 0                        //默认刷新个高度,header为空时使用
    var defaultMaxOffset: Int = 0                            //默认最大下拉高度，header为空时使用
    var durationOffset: Long = 200                           //位移动画持续时间
    var keepHeaderWhenRefresh: Boolean = true               //刷新时Header自动移动到刷新高度,false回到初始位置
    var pinContent: Boolean = false                         //下拉刷新过程是否让ContentView不发生位置移动
    var refreshEnable: Boolean = true                       //是否允许下拉刷新
    var touchSlop: Int = 0                                 //触发移动事件的最短距离
    var flingSlop: Int = 1000                              //触发Fling事件的最低速度
    var headerOffset: Int = 0                               //Header 自身消耗Offset,可处理一些特殊效果
    /*可配置参数↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/

    private var mRefreshListener: ((RefreshLayout) -> Unit)? = null
    private var mScrollListener: ((offset: Int, distance: Int, percent: Float, refreshing: Boolean) -> Unit)? = null

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop * 2

        val a = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout)
        pinContent = a.getBoolean(R.styleable.RefreshLayout_refreshPinContent, false)
        keepHeaderWhenRefresh = a.getBoolean(R.styleable.RefreshLayout_refreshKeepHeader, true)
        refreshEnable = a.getBoolean(R.styleable.RefreshLayout_refreshRefreshEnable, true)
        durationOffset = a.getInt(R.styleable.RefreshLayout_refreshDurationOffset, 200).toLong()
        defaultRefreshHeight = a.getLayoutDimension(R.styleable.RefreshLayout_refreshDefRefreshHeight, Int.MAX_VALUE)
        defaultMaxOffset = a.getLayoutDimension(R.styleable.RefreshLayout_refreshDefMaxOffset, defaultMaxOffset)
        a.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        when {
            childCount > 2 -> throw IllegalStateException("RefreshLayout can only accommodate two elements")
            childCount == 1 -> mContentView = getChildAt(0)
            childCount == 2 -> {
                mContentView = getChildAt(1)
                mHeader = getChildAt(0) as? RefreshHeader ?: return
                mHeaderView = getChildAt(0)
            }
        }
        mHeaderView?.bringToFront()
    }

    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams = LayoutParams(context, attrs)

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams = LayoutParams(p)

    @Suppress("unused")
    class LayoutParams : MarginLayoutParams {
        constructor(c: Context, attrs: AttributeSet) : super(c, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: MarginLayoutParams) : super(source)

        constructor(source: ViewGroup.LayoutParams) : super(source)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mHeaderView?.let {
            val lp = it.layoutParams as LayoutParams
            val childLeft = paddingLeft + lp.leftMargin
            val childTop = paddingTop + lp.topMargin - it.measuredHeight + mCurrentOffset + headerOffset
            val childRight = childLeft + it.measuredWidth
            val childBottom = childTop + it.measuredHeight
            it.layout(childLeft, childTop, childRight, childBottom)
        }

        mContentView?.let {
            val lp = it.layoutParams as LayoutParams
            val childLeft = paddingLeft + lp.leftMargin
            val childTop = paddingTop + lp.topMargin + if (pinContent) 0 else mCurrentOffset
            val childRight = childLeft + it.measuredWidth
            val childBottom = childTop + it.measuredHeight
            it.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        (0 until childCount).map { getChildAt(it) }.forEach { measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0) }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                cancelAnimator()
                mIsFling = false
                mLastFlingY = 0
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (!mNestedScrollExecute && !mGestureExecute) {
                    finishSpinner()
                }
            }

        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled || !refreshEnable)
            return false

        if (mNestedScrollInProgress || canChildScrollUp()) {
            return false
        }
        if (mRefreshing && pinContent && keepHeaderWhenRefresh)
            return false

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsBeingDragged = false
                mInitialDownY = ev.y
                mGesture.onTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mIsBeingDragged && ev.y - mInitialDownY > touchSlop) {
                    mIsBeingDragged = true
                }

                if (mCurrentOffset > 0 && !mIsBeingDragged) {
                    mIsBeingDragged = true
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> mIsBeingDragged = false
        }

        return mIsBeingDragged
    }


    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if ((android.os.Build.VERSION.SDK_INT < 21 && mContentView is AbsListView) || (mContentView != null && !ViewCompat.isNestedScrollingEnabled(mContentView!!))) {

        } else {
            super.requestDisallowInterceptTouchEvent(disallowIntercept)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || mNestedScrollExecute || canChildScrollUp())
            return false
        mGesture.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_UP) {
            if (!mIsFling && mGestureExecute) {
                finishSpinner()
            }
            mGestureExecute = false
        }
        return true
    }

    /**
     * 手势处理
     */
    private inner class RefreshGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            mGestureExecute = true
            val maxOffset = mHeader?.maxOffsetHeight()
                    ?: if (defaultMaxOffset == -1) height else defaultMaxOffset
            if ((mCurrentOffset == 0 && distanceY > 0) || (mCurrentOffset == maxOffset && distanceY < 0))
                return super.onScroll(e1, e2, distanceX, distanceY)
            var offset = -calculateOffset(distanceY.toInt())
            if (mCurrentOffset + offset > maxOffset) {
                offset = maxOffset - mCurrentOffset
            } else if (mCurrentOffset + offset < 0) {
                offset = -mCurrentOffset
            }
            moveView(offset)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            mGestureExecute = true
            val refreshHeight = mHeader?.refreshHeight() ?: defaultRefreshHeight
            if (velocityY > 0 && (!mRefreshing || !keepHeaderWhenRefresh || mCurrentOffset >= refreshHeight)) {
                return super.onFling(e1, e2, velocityX, velocityY)
            }
            if (Math.abs(velocityY) > flingSlop) {
                mIsFling = true
                mScroller.fling(0, 0, velocityX.toInt(), -velocityY.toInt(), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE)
                invalidate()
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    private fun canChildScrollUp(): Boolean = mContentView?.canScrollVertically(-1) ?: false

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return isEnabled && refreshEnable && !(mRefreshing && pinContent && keepHeaderWhenRefresh)
                && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        mNestedScrollExecute = false
        mNestedScrollInProgress = true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        mNestedScrollExecute = true
        if (mCurrentOffset > 0 && dy > 0) {
            val offset = if (dy > mCurrentOffset) mCurrentOffset else dy
            consumed[1] = if (dy > mCurrentOffset) dy - mCurrentOffset else dy
            moveView(-offset)
        }
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        var mUnconsumed: Int = dyUnconsumed
        val maxOffset = mHeader?.maxOffsetHeight()
                ?: if (defaultMaxOffset == -1) height else defaultMaxOffset
        if (dyUnconsumed < 0 && !canChildScrollUp() && mCurrentOffset < maxOffset) {
            if (mCurrentOffset - dyUnconsumed > maxOffset) {
                mUnconsumed = mCurrentOffset - maxOffset
            }
            val offset = -calculateOffset(mUnconsumed)
            moveView(offset)
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        mNestedScrollExecute = true

        //如果当前偏移量大于0，则交给RefreshLayout处理Fling事件
        if (mCurrentOffset > 0) {
            val refreshHeight = mHeader?.refreshHeight() ?: defaultRefreshHeight
            if (velocityY < 0 && (!mRefreshing || !keepHeaderWhenRefresh || mCurrentOffset >= refreshHeight)) {
                return true
            }
            if (abs(velocityY) > flingSlop) {
                mIsFling = true
                mScroller.fling(0, 0, velocityX.toInt(), velocityY.toInt(), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE)
                invalidate()
            }
            return true
        } else
            return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        //如果向上滚动，且处于刷新过程中，监听Fling过程
        if (mRefreshing && velocityY < -flingSlop && keepHeaderWhenRefresh) {
            mIsFling = true
            mScroller.fling(0, 0, velocityX.toInt(), velocityY.toInt(), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE)
            invalidate()
        }
        return true
    }

    override fun onStopNestedScroll(child: View) {
        if (!mIsFling && mNestedScrollExecute) {
            finishSpinner()
        }
        mNestedScrollExecute = false
        mNestedScrollInProgress = false
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset() && mIsFling) {
            //本次Fling移动距离(<0向下滚动、>0向上滚动)
            var offset = mLastFlingY - mScroller.currY
            val refreshHeight = mHeader?.refreshHeight() ?: defaultRefreshHeight
            val maxOffset = mHeader?.maxOffsetHeight()
                    ?: if (defaultMaxOffset == -1) height else defaultMaxOffset
            val mFlingMaxHeight = if (offset > 0) refreshHeight else maxOffset
            //记录上次Fling的Y值
            mLastFlingY = mScroller.currY

            if (mCurrentOffset > 0 || offset > 0 && !canChildScrollUp()) {
                offset = if (mCurrentOffset + offset > mFlingMaxHeight) mFlingMaxHeight - mCurrentOffset else if (mCurrentOffset + offset < 0) -mCurrentOffset else offset
                moveView(offset)
                if (mCurrentOffset >= mFlingMaxHeight) {
                    mScroller.forceFinished(true)
                }
            } else if (offset < 0) {
                (mContentView as? RecyclerView)?.fling(0, mScroller.currVelocity.toInt())
                (mContentView as? NestedScrollView)?.fling(mScroller.currVelocity.toInt())
                (mContentView as? ScrollView)?.fling(mScroller.currVelocity.toInt())
                mScroller.forceFinished(true)
            }
            invalidate()
        } else if (mIsFling) {
            Log.d(LOG_TAG, "mScroll fling complete mCurrentOffset is $mCurrentOffset")
            mIsFling = false
            finishSpinner()
        }
    }


    /**
     * 计算实际偏移量
     */
    private fun calculateOffset(offset: Int): Int {
        val maxOffset = mHeader?.maxOffsetHeight()
                ?: if (defaultMaxOffset == -1) height else defaultMaxOffset
        val mOffset: Int
        val downResistance: Float
        downResistance = if (offset > 0) {
            0.8f
        } else {
            //下拉阻力(0f-1f) 越小阻力越大，当前计算公式:1-mCurrentOffset/maxheight
            1f - mCurrentOffset.toFloat() / maxOffset
        }
        mOffset = if (offset > 0) {
            MAX_OFFSET.coerceAtMost(ceil((downResistance * offset).toDouble()).toInt())
        } else {
            (-MAX_OFFSET).coerceAtLeast(floor((downResistance * offset).toDouble()).toInt())
        }
        return mOffset
    }

    /**
     * 移动视图
     * @param offset 偏移量
     */
    private fun moveView(offset: Int) {
        var invalidate = false
        val refreshHeight = mHeader?.refreshHeight() ?: defaultRefreshHeight
        if (!mRefreshing && mCurrentOffset == 0 && offset > 0) {
            mHeader?.onPrepare(this)
        }

        if (mCurrentOffset > height || mCurrentOffset == 0) {
            invalidate = true
        }

        mCurrentOffset += offset
        mHeaderView?.offsetTopAndBottom(offset)
        if (!pinContent)
            mContentView?.offsetTopAndBottom(offset)
        if (invalidate) invalidate()
        mHeader?.onScroll(this, mCurrentOffset, mCurrentOffset.toFloat() / refreshHeight, mRefreshing)
        mScrollListener?.invoke(offset, mCurrentOffset, mCurrentOffset.toFloat() / refreshHeight, mRefreshing)

        if (!mRefreshing && offset < 0 && mCurrentOffset == 0) {
            mHeader?.onReset(this)
            mIsReset = true
        }
    }

    /**
     * 取消offset动画
     */
    private fun cancelAnimator() {
        if (mOffsetAnimator.isRunning) {
            mOffsetAnimator.cancel()
        }
    }

    /**
     * 结束下拉
     */
    private fun finishSpinner() {
        if (mCurrentOffset <= 0) return
        Log.d(LOG_TAG, "finishSpinner mCurrentOffset is $mCurrentOffset , mRefreshing is $mRefreshing")
        val target: Int
        val refreshHeight = mHeader?.refreshHeight() ?: defaultRefreshHeight
        if (mRefreshing) {
            target = if (mCurrentOffset >= refreshHeight / 2) refreshHeight else 0
        } else {
            target = if (mCurrentOffset >= refreshHeight && mIsReset) refreshHeight else 0
            if (mCurrentOffset >= refreshHeight && mIsReset) {
                mRefreshing = true//开始刷新
                mIsReset = false
                mHeader?.onRefresh(this)
                mRefreshListener?.invoke(this)
            }
        }
        animTo(target)
    }


    /**
     * 动画方式移动

     * @param target 目标位置
     */
    private fun animTo(target: Int) {
        cancelAnimator()

        val mTarget: Int = if (keepHeaderWhenRefresh) target else 0

        if (mCurrentOffset == mTarget) {
            return
        }

        Log.d(LOG_TAG, "animTo $mCurrentOffset to $mTarget")
        mOffsetAnimator.duration = durationOffset
        mOffsetAnimator.setIntValues(mCurrentOffset, mTarget)
        mOffsetAnimator.start()
    }

    fun setRefreshListener(refreshListener: (refreshLayout: RefreshLayout) -> Unit) {
        mRefreshListener = refreshListener
    }

    /**
     * offset(本次的偏移量)
     * distance(总的偏移量)
     * percent(偏移比率)
     * refreshing(是否在刷新)
     */
    fun setScrollListener(scrollListener: (offset: Int, distance: Int, percent: Float, refreshing: Boolean) -> Unit) {
        mScrollListener = scrollListener
    }

    fun setHeader(headerView: RefreshHeader) {
        setHeader(headerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setHeader(headerView: RefreshHeader, width: Int, height: Int) {
        val params = generateDefaultLayoutParams()
        params.width = width
        params.height = height
        setHeader(headerView, params)
    }

    fun setHeader(headerView: RefreshHeader, params: LayoutParams) {
        removeHeader()
        mHeader = headerView
        mHeaderView = mHeader as? View
        addView(mHeaderView, 0, params)
        mHeaderView?.bringToFront()
    }

    fun getHeader(): RefreshHeader? = mHeader

    fun removeHeader() {
        removeView(mHeaderView)
    }

    /**
     * 自动刷新
     */
    fun startRefresh() {
        if (!mRefreshing && refreshEnable) {
            postDelayed({
                mRefreshing = true
                mIsReset = false
                mHeader?.onRefresh(this)
                mRefreshListener?.invoke(this)
                mContentView?.scrollTo(0, 0)
                animTo(mHeader?.refreshHeight() ?: defaultRefreshHeight)
            }, 100)
        }
    }

    /**
     * 刷新完成
     */
    fun refreshComplete(isSuccess: Boolean) {
        if (mRefreshing) {
            mHeader?.onComplete(this, isSuccess)
            mRefreshing = false
            if (mCurrentOffset == 0) {
                mIsReset = true
                cancelAnimator()
                mHeader?.onReset(this)
            } else {
                //停滞时间
                val retention = if (isSuccess) mHeader?.succeedRetention()
                        ?: 0 else mHeader?.failingRetention() ?: 0
                postDelayed({
                    animTo(0)
                }, retention)
            }
        }
    }
}