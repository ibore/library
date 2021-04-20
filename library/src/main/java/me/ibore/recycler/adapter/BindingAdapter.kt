package me.ibore.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.ibore.recycler.holder.BindingHolder
import me.ibore.utils.BindingUtils

abstract class BindingAdapter<VB : ViewBinding, D> : RecyclerAdapter<BindingHolder<VB>, D>() {

    override fun onCreateHolder(parent: ViewGroup, dataType: Int): BindingHolder<VB> {
        val vb: VB = BindingUtils.reflexViewBinding(javaClass, LayoutInflater.from(parent.context), parent, false)
        return BindingHolder(vb)
    }

    override fun onBindHolder(
        holder: BindingHolder<VB>, data: D, dataPosition: Int, viewType: Int
    ) {
        holder.binding.onBindingHolder(holder,  data, dataPosition)
    }

    abstract fun VB.onBindingHolder(holder: BindingHolder<VB>, data: D, dataPosition: Int)

}
