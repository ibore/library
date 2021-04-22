package me.ibore.recycler.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.ibore.ktx.layoutInflater
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.utils.ReflexUtils

abstract class BindingAdapter<VB : ViewBinding, D> : RecyclerAdapter<D>() {

    override fun onCreateHolder(parent: ViewGroup, dataType: Int): RecyclerHolder {
        val vb: VB = ReflexUtils.viewBinding(javaClass, parent.layoutInflater, parent, false)
        return RecyclerHolder(vb.root, vb)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindHolder(holder: RecyclerHolder, data: D, dataPosition: Int, viewType: Int) {
        (holder.helper as VB).onBindHolder(holder, data, dataPosition)
    }

    abstract fun VB.onBindHolder(holder: RecyclerHolder, data: D, dataPosition: Int)

}
