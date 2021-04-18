//package me.ibore.widget
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.MotionEvent
//import android.view.ViewGroup
//import android.widget.RelativeLayout
//import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
//import androidx.recyclerview.widget.RecyclerView.ItemDecoration
//import androidx.viewpager2.widget.CompositePageTransformer
//import androidx.viewpager2.widget.MarginPageTransformer
//import androidx.viewpager2.widget.ViewPager2
//import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
//import me.ibore.recycler.adapter.RecyclerAdapter
//import me.ibore.recycler.holder.RecyclerHolder
//import me.ibore.widget.indicator.Indicator
//import kotlin.math.abs
//
//class BannerView<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
//
//    private var changeCallback: OnPageChangeCallback? = null
//    private var compositePageTransformer: CompositePageTransformer? = null
//    private var bannerAdapterWrapper: BannerAdapterWrapper<T>? = null
//    private var holderRestLoader: HolderRestLoader? = null
//    var viewPager2: ViewPager2? = null
//        private set
//    private var indicator: Indicator? = null
//    private var isAutoPlay = true
//    private var autoTurningTime = DEFAULT_AUTO_TIME
//    private var currentPage = 0
//    private var realCount = 0
//    private var needCount = 0
//    private var sidePage = 0
//    private var needPage = NORMAL_COUNT
//
//    init {
//        viewPager2 = ViewPager2(context)
//        viewPager2!!.layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//        viewPager2!!.setPageTransformer(CompositePageTransformer().also { compositePageTransformer = it })
//        bannerAdapterWrapper = BannerAdapterWrapper()
//        viewPager2!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                val realPosition = toRealPosition(position)
//                changeCallback?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
//                indicator?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
//            }
//
//            override fun onPageSelected(position: Int) {
//                val resetItem = currentPage == sidePage - 1 || currentPage == needCount - (sidePage - 1) || position != currentPage && needCount - currentPage == sidePage
//                currentPage = position
//                val realPosition = toRealPosition(position)
//                holderRestLoader?.onItemRestLoader(realPosition, resetItem)
//                if (resetItem) return
//                changeCallback?.onPageSelected(realPosition)
//                indicator?.onPageSelected(realPosition)
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//                changeCallback?.onPageScrollStateChanged(state)
//                indicator?.onPageScrollStateChanged(state)
//                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
//                    if (currentPage == sidePage - 1) {
//                        viewPager2!!.setCurrentItem(realCount + currentPage, false)
//                    } else if (currentPage == needCount - sidePage) {
//                        viewPager2!!.setCurrentItem(sidePage, false)
//                    }
//                }
//            }
//        })
//        val recyclerView = viewPager2!!.getChildAt(0) as RecyclerView
//        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//        addView(viewPager2)
//    }
//
//    private fun startPager(startPosition: Int) {
//        val adapter = viewPager2!!.adapter
//        if (adapter == null || sidePage == NORMAL_COUNT) {
//            viewPager2!!.adapter = bannerAdapterWrapper
//        } else {
//            adapter.notifyDataSetChanged()
//        }
//        currentPage = startPosition + sidePage
//        viewPager2!!.isUserInputEnabled = realCount > 1
//        viewPager2!!.setCurrentItem(currentPage, false)
//        indicator?.initIndicatorCount(realCount)
//        if (isAutoPlay) {
//            startTurning()
//        }
//    }
//
//    private fun initPagerCount() {
//        val adapter = bannerAdapterWrapper!!.adapter
//        if (adapter == null || adapter.itemCount == 0) {
//            realCount = 0
//            needCount = 0
//            return
//        }
//        realCount = adapter.itemCount
//        sidePage = needPage / NORMAL_COUNT
//        needCount = realCount + needPage
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        if (isAutoPlay) {
//            startTurning()
//        }
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        if (isAutoPlay) {
//            stopTurning()
//        }
//    }
//
//    private val task: Runnable = object : Runnable {
//        override fun run() {
//            if (isAutoPlay && realCount > 1) {
//                currentPage++
//                if (currentPage == realCount + sidePage + 1) {
//                    viewPager2!!.setCurrentItem(sidePage, false)
//                    post(this)
//                } else {
//                    viewPager2!!.currentItem = currentPage
//                    postDelayed(this, autoTurningTime)
//                }
//            }
//        }
//    }
//
//    private fun toRealPosition(position: Int): Int {
//        var realPosition = 0
//        if (realCount != 0) {
//            realPosition = (position - sidePage) % realCount
//        }
//        if (realPosition < 0) {
//            realPosition += realCount
//        }
//        return realPosition
//    }
//
//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        if (isAutoPlay) {
//            val action = ev.action
//            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
//                startTurning()
//            } else if (action == MotionEvent.ACTION_DOWN) {
//                stopTurning()
//            }
//        }
//        return super.dispatchTouchEvent(ev)
//    }
//
//    private inner class BannerAdapterWrapper<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//        var adapter: RecyclerAdapter<T>? = null
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            return adapter!!.onCreateViewHolder(parent, viewType)
//        }
//
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            adapter!!.onBindViewHolder(holder as RecyclerHolder, toRealPosition(position))
//        }
//
//        override fun getItemViewType(position: Int): Int {
//            return adapter!!.getItemViewType(toRealPosition(position))
//        }
//
//        override fun getItemId(position: Int): Long {
//            return adapter!!.getItemId(toRealPosition(position))
//        }
//
//        override fun getItemCount(): Int {
//            return needCount
//        }
//
//        fun registerAdapter(adapter: RecyclerAdapter<T>?) {
//            this.adapter?.unregisterAdapterDataObserver(itemDataSetChangeObserver)
//            this.adapter = adapter
//            this.adapter?.registerAdapterDataObserver(itemDataSetChangeObserver)
//        }
//    }
//
//    private val itemDataSetChangeObserver: AdapterDataObserver = object : AdapterDataObserver() {
//        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) { onChanged() }
//        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) { onChanged() }
//        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) { onChanged() }
//        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) { onChanged() }
//        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) { onChanged() }
//        override fun onChanged() {
//            if (viewPager2 != null && bannerAdapterWrapper != null) {
//                initPagerCount()
//                startPager(currentPager)
//            }
//        }
//    }
//    /*--------------- 下面是对外暴露的方法 ---------------*/
//    /**
//     * 设置一屏多页
//     *
//     * @param multiWidth 左右页面露出来的宽度一致
//     * @param pageMargin item与item之间的宽度
//     */
//    fun setPageMargin(multiWidth: Int, pageMargin: Int): BannerView<T> {
//        return setPageMargin(multiWidth, multiWidth, pageMargin)
//    }
//
//    /**
//     * 设置一屏多页
//     *
//     * @param tlWidth    左边页面显露出来的宽度
//     * @param brWidth    右边页面露出来的宽度
//     * @param pageMargin item与item之间的宽度
//     */
//    fun setPageMargin(tlWidth: Int, brWidth: Int, pageMargin: Int): BannerView<T> {
//        if (pageMargin != 0) {
//            compositePageTransformer!!.addTransformer(MarginPageTransformer(pageMargin))
//        }
//        if (tlWidth > 0 && brWidth > 0) {
//            val recyclerView = viewPager2!!.getChildAt(0) as RecyclerView
//            if (viewPager2!!.orientation == ViewPager2.ORIENTATION_VERTICAL) {
//                recyclerView.setPadding(0, tlWidth + abs(pageMargin), 0, brWidth + abs(pageMargin))
//            } else {
//                recyclerView.setPadding(tlWidth + abs(pageMargin), 0, brWidth + abs(pageMargin), 0)
//            }
//            recyclerView.clipToPadding = false
//            setOffscreenPageLimit(1)
//            needPage += NORMAL_COUNT
//        }
//        return this
//    }
//
//    fun setPageTransformer(transformer: ViewPager2.PageTransformer?): BannerView<T> {
//        compositePageTransformer!!.addTransformer(transformer!!)
//        return this
//    }
//
//    fun setAutoTurningTime(autoTurningTime: Long): BannerView<T> {
//        this.autoTurningTime = autoTurningTime
//        return this
//    }
//
//    fun setOuterPageChangeListener(listener: OnPageChangeCallback?): BannerView<T> {
//        changeCallback = listener
//        return this
//    }
//
//    fun setOffscreenPageLimit(limit: Int): BannerView<T> {
//        viewPager2!!.offscreenPageLimit = limit
//        return this
//    }
//
//    /**
//     * 设置轮播方向
//     *
//     * @param orientation Orientation.ORIENTATION_HORIZONTAL or default
//     * Orientation.ORIENTATION_VERTICAL
//     */
//    fun setOrientation(@ViewPager2.Orientation orientation: Int): BannerView<T> {
//        viewPager2!!.orientation = orientation
//        return this
//    }
//
//    fun addItemDecoration(decor: ItemDecoration): BannerView<T> {
//        viewPager2!!.addItemDecoration(decor)
//        return this
//    }
//
//    fun addItemDecoration(decor: ItemDecoration, index: Int): BannerView<T> {
//        viewPager2!!.addItemDecoration(decor, index)
//        return this
//    }
//
//    /**
//     * 是否自动轮播 大于1页轮播才生效
//     */
//    fun setAutoPlay(autoPlay: Boolean): BannerView<T> {
//        isAutoPlay = autoPlay
//        if (isAutoPlay && realCount > 1) {
//            startTurning()
//        }
//        return this
//    }
//
//    fun isAutoPlay(): Boolean {
//        return isAutoPlay && realCount > 1
//    }
//
//    fun setIndicator(indicator: Indicator?): BannerView<T> {
//        return setIndicator(indicator, true)
//    }
//
//    /**
//     * 设置indicator，支持在xml中创建
//     *
//     * @param attachToRoot true 添加到banner布局中
//     */
//    fun setIndicator(indicator: Indicator?, attachToRoot: Boolean): BannerView<T> {
//        if (this.indicator != null) {
//            removeView(this.indicator!!.view!!)
//        }
//        if (indicator != null) {
//            this.indicator = indicator
//            if (attachToRoot) {
//                addView(this.indicator!!.view!!, this.indicator!!.params!!)
//            }
//        }
//        return this
//    }
//
//    fun setHolderRestLoader(holderRestLoader: HolderRestLoader?): BannerView<T> {
//        this.holderRestLoader = holderRestLoader
//        return this
//    }
//
//    /**
//     * 返回真实位置
//     */
//    val currentPager: Int
//        get() {
//            val position = toRealPosition(currentPage)
//            return position.coerceAtLeast(0)
//        }
//
//    var adapter: RecyclerAdapter<T>?
//        get() = bannerAdapterWrapper!!.adapter
//        set(adapter) {
//            setAdapter(adapter, 0)
//        }
//
//    fun startTurning() {
//        stopTurning()
//        postDelayed(task, autoTurningTime)
//    }
//
//    fun stopTurning() {
//        removeCallbacks(task)
//    }
//
//    fun setAdapter(adapter: RecyclerAdapter<T>?, startPosition: Int) {
//        bannerAdapterWrapper!!.registerAdapter(adapter)
//        initPagerCount()
//        startPager(startPosition)
//    }
//
//    companion object {
//        private const val DEFAULT_AUTO_TIME: Long = 2500
//        private const val NORMAL_COUNT = 2
//    }
//
//    interface HolderRestLoader {
//        /**
//         * 作为ViewPager2扩展接口，页面切换时调用
//         *
//         * @param position   当前的position
//         * @param isRestItem 是否是ViewPager重置当前位置，
//         * 也就是当滑动的最后一页时或者是滑动到第一页，通过setCurrentItem(position,false)重新设置ViewPager的位置时。
//         * 每当发生ViewPager重写设置当前位置时，isRestItem = true
//         */
//        fun onItemRestLoader(position: Int, isRestItem: Boolean)
//    }
//
//}
