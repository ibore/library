package me.ibore.recycler.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.ibore.animation.BaseAnimation
import me.ibore.recycler.holder.ItemHolder
import me.ibore.recycler.holder.LoadHolder
import me.ibore.recycler.holder.LoadMoreHolder
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.recycler.listener.*
import me.ibore.utils.AnimatorUtils


@Suppress("UNREACHABLE_CODE")
abstract class  RecyclerAdapter<VH : RecyclerHolder, D> : RecyclerView.Adapter<RecyclerHolder>(),
    IRecyclerAdapter<VH, D> {

    companion object {
        // 头布局
        private const val HEADER = 9000 - 1

        // 脚布局
        private const val FOOTER = HEADER - 1

        // 状态布局
        private const val STATUS = FOOTER - 1

        // 加载更多布局
        private const val MORE = STATUS - 1

        const val STATUS_LOAD: Int = 1
        const val STATUS_EMPTY: Int = 2
        const val STATUS_ERROR: Int = 3
    }

    // 点击事件监听
    open var onItemClickListener: OnItemClickListener<VH, D>? = null
    open var onItemLongClickListener: OnItemLongClickListener<VH, D>? = null
    open var onItemChildClickListener: OnItemChildClickListener<VH, D>? = null
    open var onItemChildLongClickListener: OnItemChildLongClickListener<VH, D>? = null
    open var onLoadListener: OnLoadListener? = null
    open var onLoadMoreListener: OnLoadMoreListener? = null

    private var datas: MutableList<D> = ArrayList()
    private var showItem: Boolean = true
    private var headerHolder: ItemHolder? = null
    private var loadHolder: LoadHolder? = null
    private var footerHolder: ItemHolder? = null
    private var loadMoreHolder: LoadMoreHolder? = null

    private var showContent = false
    private var canLoadingMore = false

    private var animation: BaseAnimation? = null
    private var animatorLastPosition = -1
    private var animatorFirstOnly = true
    private var animatorDuration: Long? = null
    private var animatorInterpolator: Interpolator? = null

    override fun setDatas(datas: MutableList<D>) {
        if (datas.isNullOrEmpty()) {
            clearDatas()
        } else {
            this.datas = datas
            animatorLastPosition = -1
            notifyDataSetChanged()
        }
    }

    override fun addDatas(datas: MutableList<D>) {
        if (datas.isNullOrEmpty()) return
        this.datas.addAll(datas)
    }

    override fun addData(data: D) {
        datas.add(data)
        notifyItemInserted(if (hasHeaderHolder) datas.size else datas.size - 1)
    }

    override fun addData(data: D, dataPosition: Int) {
        datas.add(dataPosition, data)
        val adapterPosition = if (hasHeaderHolder) dataPosition + 1 else dataPosition
        notifyItemInserted(adapterPosition)
    }

    override fun getData(dataPosition: Int): D {
        return datas[dataPosition]
    }

    override fun getDatas(): MutableList<D> {
        return datas
    }

    override fun getDataSize(): Int {
        return datas.size
    }

    override fun removeData(dataPosition: Int) {
        datas.removeAt(dataPosition)
        val position = if (hasHeaderHolder) dataPosition + 1 else dataPosition
        notifyItemRangeChanged(position, itemCount - position)
    }

    override fun removeData(data: D) {
        val index = datas.indexOf(data)
        if (index >= 0) {
            removeData(index)
        }
    }

    override fun clearDatas() {
        datas.clear()
        animatorLastPosition = -1
        notifyDataSetChanged()
    }

    open fun setAnimatorFirstOnly(firstOnly: Boolean) {
        this.animatorFirstOnly = firstOnly
    }

    @JvmOverloads
    open fun setAnimation(
        animation: BaseAnimation, duration: Long? = null, value: Interpolator? = null
    ) {
        this.animation = animation
        this.animatorDuration = duration
        this.animatorInterpolator = value
    }

    open fun clearAnimator(v: View) {
        AnimatorUtils.reset(v)
    }

    @Suppress("UNCHECKED_CAST")
    final override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        when (getItemViewType(position)) {
            STATUS -> loadHolder?.onBindRecyclerHolder(holder)
            HEADER -> headerHolder?.onBindHolder(holder)
            FOOTER -> footerHolder?.onBindHolder(holder)
            MORE -> loadMoreHolder?.onBindRecyclerHolder(holder)
            else -> {
                val dataPosition = getDataPosition(position)
                val data = getData(dataPosition)
                holder.itemView.setOnClickListener {
                    onItemClickListener?.onItemClick(holder as VH, data, dataPosition)
                }
                holder.itemView.setOnLongClickListener {
                    onItemLongClickListener?.onItemLongClick(holder as VH, data, dataPosition) ?: false
                }
                onBindHolder(
                    holder as VH,
                    getData(dataPosition),
                    dataPosition,
                    getItemViewType(position)
                )
                if (animation != null && (!animatorFirstOnly || dataPosition > animatorLastPosition)) {
                    for (anim in animation!!.getAnimators(holder.itemView)) {
                        animatorDuration?.let { anim.duration = it }
                        anim.start()
                    }
                    animatorLastPosition = dataPosition
                } else {
                    clearAnimator(holder.itemView)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return when (viewType) {
            STATUS -> loadHolder!!.onCreateHolder(parent)
            HEADER -> headerHolder!!.onCreateHolder(parent)
            FOOTER -> footerHolder!!.onCreateHolder(parent)
            MORE -> loadMoreHolder!!.onCreateHolder(parent)
            else -> onCreateHolder(parent, viewType)
        }
    }

    final override fun getItemCount(): Int {
        if (showStatusView) return 1
        var itemCount = getDataSize()
        if (hasHeaderHolder) itemCount++
        if (hasFooterHolder) itemCount++
        if (null != loadMoreHolder) itemCount++
        return itemCount
    }

    final override fun getItemViewType(position: Int): Int {
        return when {
            showStatusView -> STATUS
            isHeaderHolder(position) -> HEADER
            isFooterHolder(position) -> FOOTER
            isMoreHolder(position) -> MORE
            else -> getDataType(datas[getDataPosition(position)], getDataPosition(position))
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerHolder) {
        super.onViewAttachedToWindow(holder)
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            when {
                showStatusView -> layoutParams.isFullSpan = true
                isHeaderHolder(holder.layoutPosition) -> layoutParams.isFullSpan = true
                isFooterHolder(holder.layoutPosition) -> layoutParams.isFullSpan = true
                isMoreHolder(holder.layoutPosition) -> layoutParams.isFullSpan = true
                else -> layoutParams.isFullSpan = isStaggeredFullSpan(
                    getData(getDataPosition(holder.layoutPosition)),
                    getDataPosition(holder.layoutPosition)
                )
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (showStatusView || isHeaderHolder(position) || isMoreHolder(position)) {
                        layoutManager.spanCount
                    } else {
                        getGridSpanSize(
                            layoutManager.spanCount,
                            getData(getDataPosition(position)),
                            getDataPosition(position)
                        )
                    }
                }
            }
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onLoadingMore(layoutManager)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onLoadingMore(layoutManager)
            }
        })
    }

    protected open fun onLoadingMore(layoutManager: RecyclerView.LayoutManager?) {
        if (null != onLoadMoreListener && null != loadMoreHolder && loadMoreHolder!!.status == STATUS_LOAD
            && null != layoutManager && canLoadingMore
        ) {
            val lastVisibleItem = when (layoutManager) {
                is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                is StaggeredGridLayoutManager -> {
                    val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                    var max = lastVisibleItemPositions[0]
                    for (value in lastVisibleItemPositions) {
                        if (value > max) max = value
                    }
                    max
                }
                else -> -1
            }
            val totalItemCount = layoutManager.itemCount
            if (lastVisibleItem == totalItemCount - 1) {
                canLoadingMore = false
                onLoadMoreListener?.onLoadMoreLoading()
            }
        }
    }

    protected fun isStaggeredFullSpan(data: D, dataPosition: Int): Boolean = false

    protected open fun getGridSpanSize(spanCount: Int, data: D, dataPosition: Int): Int = 1

    val hasHeaderHolder: Boolean
        get() = null != headerHolder

    val hasFooterHolder: Boolean
        get() = null != footerHolder

    val showStatusView: Boolean
        get() = !showItem && null != loadHolder

    private fun isHeaderHolder(position: Int): Boolean = position == 0 && hasHeaderHolder

    private fun isFooterHolder(position: Int): Boolean {
        if (null == footerHolder) return false
        var po = position
        if (hasHeaderHolder) po -= 1
        return po == datas.size
    }

    private fun isMoreHolder(position: Int): Boolean {
        if (null == loadMoreHolder) return false
        var po = position
        if (hasHeaderHolder) po -= 1
        if (hasFooterHolder) po -= 1
        return po == datas.size
    }

    private fun getMoreHolderPosition(): Int {
        return if (showItem && null != loadMoreHolder) {
            var position = datas.size
            if (hasHeaderHolder) position++
            if (hasFooterHolder) position++
            position
        } else -1
    }

    private fun getDataPosition(position: Int): Int {
        return if (hasHeaderHolder) position - 1 else position
    }

    override fun getDataType(data: D, dataPosition: Int): Int = 0

    open fun setLoadHolder(context: Context, loadingId: Int, emptyId: Int, errorId: Int) {
        setLoadHolder(LoadHolder.create(context, loadingId, emptyId, errorId))
    }

    open fun setLoadHolder(loadingView: View, emptyView: View, errorView: View) {
        setLoadHolder(LoadHolder.create(loadingView, emptyView, errorView))
    }

    open fun setLoadHolder(loadHolder: LoadHolder) {
        this.loadHolder = loadHolder
        if (!showItem) notifyDataSetChanged()
    }

    open fun removeLoadHolder() {
        if (null != loadHolder) {
            loadHolder = null
            notifyDataSetChanged()
        }
    }

    open fun showItemView() {
        showItem = true
        notifyDataSetChanged()
    }

    open fun showLoadingView() {
        if (null != loadHolder) {
            showItem = false
            loadHolder!!.status = STATUS_LOAD
            canLoadingMore = false
            notifyDataSetChanged()
        }
    }

    open fun showEmptyView() {
        if (null != loadHolder) {
            showItem = false
            loadHolder!!.status = STATUS_EMPTY
            loadHolder!!.onLoadListener = onLoadListener
            canLoadingMore = false
            notifyDataSetChanged()
        }
    }

    open fun showErrorView() {
        if (null != loadHolder) {
            showItem = false
            loadHolder!!.status = STATUS_ERROR
            loadHolder!!.onLoadListener = onLoadListener
            canLoadingMore = false
            notifyDataSetChanged()
        }
    }

    open fun setLoadingMoreHolder(context: Context, loadingId: Int, emptyId: Int, errorId: Int) {
        setLoadingMoreHolder(LoadMoreHolder.create(context, loadingId, emptyId, errorId))
    }

    open fun setLoadingMoreHolder(loadingView: View, emptyView: View, errorView: View) {
        setLoadingMoreHolder(LoadMoreHolder.create(loadingView, emptyView, errorView))
    }

    open fun setLoadingMoreHolder(loadMoreHolder: LoadMoreHolder) {
        this.loadMoreHolder = loadMoreHolder
        if (showItem) notifyDataSetChanged()
    }

    open fun removeLoadMoreHolder() {
        if (null != loadMoreHolder) {
            loadMoreHolder = null
            notifyDataSetChanged()
        }
    }

    open fun showLoadingMoreView() {
        if (null != loadMoreHolder && showItem) {
            loadMoreHolder!!.status = STATUS_LOAD
            notifyItemChanged(getMoreHolderPosition())
        }
    }

    open fun showEmptyMoreView() {
        if (null != loadMoreHolder && showItem) {
            loadMoreHolder!!.status = STATUS_EMPTY
            notifyItemChanged(getMoreHolderPosition())
        }
    }

    open fun showErrorMoreView() {
        if (null != loadMoreHolder && showItem) {
            loadMoreHolder!!.status = STATUS_ERROR
            loadMoreHolder!!.onLoadMoreListener = onLoadMoreListener
            notifyItemChanged(getMoreHolderPosition())
        }
    }

    open fun setHeaderHolder(headerHolder: ItemHolder) {
        this.headerHolder = headerHolder
        notifyDataSetChanged()
    }

    open fun setFooterHolder(footerHolder: ItemHolder) {
        this.footerHolder = footerHolder
        notifyDataSetChanged()
    }

    open fun removeHeaderHolder( ) {
        if (null != headerHolder) {
            this.headerHolder = null
            notifyDataSetChanged()
        }
    }

    open fun removeFooterHolder() {
        if (null != headerHolder) {
            this.footerHolder = null
            notifyDataSetChanged()
        }
    }

}