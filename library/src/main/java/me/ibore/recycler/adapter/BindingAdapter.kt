package me.ibore.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.ibore.recycler.holder.BindingHolder
import me.ibore.utils.ViewBindingUtils

abstract class BindingAdapter<VB : ViewBinding, D> : RecyclerAdapter<BindingHolder<VB>, D>() {

    override fun onCreateHolder(parent: ViewGroup, dataType: Int): BindingHolder<VB> {
        val vb: VB =
            ViewBindingUtils.inflate<VB>(this, LayoutInflater.from(parent.context), parent)!!
        return BindingHolder(vb)
    }

    override fun onBindHolder(
        holder: BindingHolder<VB>,
        data: D,
        dataPosition: Int,
        viewType: Int
    ) {
        onBindingHolder(holder, holder.binding, data, dataPosition)
    }

    abstract fun onBindingHolder(holder: BindingHolder<VB>, binding: VB, data: D, dataPosition: Int)

}