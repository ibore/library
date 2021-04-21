package me.ibore.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.utils.BindingUtils

abstract class BindingAdapter<VB : ViewBinding, D> : RecyclerAdapter<D>() {

    override fun onCreateHolder(parent: ViewGroup, dataType: Int): RecyclerHolder {
        val vb: VB = BindingUtils.reflexViewBinding(
            javaClass, LayoutInflater.from(parent.context), parent, false
        )
        val holder = RecyclerHolder(vb.root)
        holder.extra = vb
        return holder
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindHolder(holder: RecyclerHolder, data: D, dataPosition: Int, viewType: Int) {
        (holder.extra as VB).onBindingHolder(holder, data, dataPosition)
    }

    abstract fun VB.onBindingHolder(holder: RecyclerHolder, data: D, dataPosition: Int)

}
