package me.ibore.recycler.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import me.ibore.recycler.adapter.RecyclerAdapter
import me.ibore.recycler.listener.OnLoadMoreListener
import me.ibore.utils.SizeUtils

class LoadMoreHolder : ItemHolder {

    companion object {
        fun create(context: Context, loadingId: Int, emptyId: Int, errorId: Int): LoadMoreHolder {
            return LoadMoreHolder(context, loadingId, emptyId, errorId)
        }

        fun create(loadingView: View, emptyView: View, errorView: View): LoadMoreHolder {
            return LoadMoreHolder(loadingView, emptyView, errorView)
        }
    }


    private val loadingView: View
    private val emptyView: View
    private val errorView: View

    var status: Int = RecyclerAdapter.STATUS_LOAD
    var onLoadMoreListener: OnLoadMoreListener? = null

    constructor(context: Context, loadingId: Int, emptyId: Int, errorId: Int) {
        this.loadingView = LayoutInflater.from(context).inflate(loadingId, null)
        this.emptyView = LayoutInflater.from(context).inflate(emptyId, null)
        this.errorView = LayoutInflater.from(context).inflate(errorId, null)
    }

    constructor(loadingView: View, emptyView: View, errorView: View) {
        this.loadingView = loadingView
        this.emptyView = emptyView
        this.errorView = errorView
    }

    override fun onCreateHolder(parent: ViewGroup): RecyclerHolder {
        val frameLayout = FrameLayout(parent.context)
        frameLayout.layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        frameLayout.addView(loadingView)
        frameLayout.addView(emptyView)
        frameLayout.addView(errorView)
        return RecyclerHolder.create(frameLayout)
    }

    override  fun onBindHolder(holder: RecyclerHolder) {
        val frameLayout = holder.itemView as FrameLayout
        when (status) {
            RecyclerAdapter.STATUS_LOAD -> {
                frameLayout.getChildAt(0).visibility = View.VISIBLE
                frameLayout.getChildAt(1).visibility = View.GONE
                frameLayout.getChildAt(2).visibility = View.GONE
            }
            RecyclerAdapter.STATUS_EMPTY -> {
                frameLayout.getChildAt(0).visibility = View.GONE
                frameLayout.getChildAt(1).visibility = View.VISIBLE
                frameLayout.getChildAt(2).visibility = View.GONE
            }
            RecyclerAdapter.STATUS_ERROR -> {
                frameLayout.getChildAt(0).visibility = View.GONE
                frameLayout.getChildAt(1).visibility = View.GONE
                frameLayout.getChildAt(2).visibility = View.VISIBLE
                frameLayout.getChildAt(2).setOnClickListener { v: View? ->
                    if (null != onLoadMoreListener) {
                        onLoadMoreListener!!.onLoadMoreError()
                    }
                }
            }
        }
    }

}