package me.ibore.recycler.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import me.ibore.recycler.adapter.RecyclerAdapter.Companion.STATUS_EMPTY
import me.ibore.recycler.adapter.RecyclerAdapter.Companion.STATUS_ERROR
import me.ibore.recycler.adapter.RecyclerAdapter.Companion.STATUS_LOAD
import me.ibore.recycler.listener.OnLoadListener

class LoadHolder {

    companion object {
        fun create(context: Context, loadingId: Int, emptyId: Int, errorId: Int): LoadHolder {
            return LoadHolder(context, loadingId, emptyId, errorId)
        }

        fun create(loadingView: View, emptyView: View, errorView: View): LoadHolder {
            return LoadHolder(loadingView, emptyView, errorView)
        }
    }

    private val loadingView: View
    private val emptyView: View
    private val errorView: View

    var status: Int = STATUS_LOAD
    var onLoadListener: OnLoadListener? = null

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

    fun onCreateHolder(parent: ViewGroup): RecyclerHolder {
        val frameLayout = FrameLayout(parent.context)
        frameLayout.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        frameLayout.addView(loadingView)
        frameLayout.addView(emptyView)
        frameLayout.addView(errorView)
        return RecyclerHolder.create(frameLayout)
    }

    fun onBindRecyclerHolder(holder: RecyclerHolder) {
        val frameLayout = holder.itemView as FrameLayout
        when (status) {
            STATUS_LOAD -> {
                frameLayout.getChildAt(0).visibility = View.VISIBLE
                frameLayout.getChildAt(1).visibility = View.GONE
                frameLayout.getChildAt(2).visibility = View.GONE
            }
            STATUS_EMPTY -> {
                frameLayout.getChildAt(0).visibility = View.GONE
                frameLayout.getChildAt(1).visibility = View.VISIBLE
                frameLayout.getChildAt(2).visibility = View.GONE
                frameLayout.getChildAt(1).setOnClickListener { onLoadListener?.onLoadEmpty() }
            }
            STATUS_ERROR -> {
                frameLayout.getChildAt(0).visibility = View.GONE
                frameLayout.getChildAt(1).visibility = View.GONE
                frameLayout.getChildAt(2).visibility = View.VISIBLE
                frameLayout.getChildAt(2).setOnClickListener { onLoadListener?.onLoadError() }
            }
        }
    }

}