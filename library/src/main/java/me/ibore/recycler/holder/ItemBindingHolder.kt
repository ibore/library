package me.ibore.recycler.holder

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.ibore.ktx.layoutInflater
import me.ibore.utils.ReflexUtils

abstract class ItemBindingHolder<VB : ViewBinding> : ItemHolder {

    override fun onCreateHolder(parent: ViewGroup): RecyclerHolder {
        val vb: VB = ReflexUtils.viewBinding(javaClass, parent.layoutInflater, parent, false)
        val holder = RecyclerHolder(vb.root)
        holder.extra = vb
        return holder
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindHolder(holder: RecyclerHolder) {
        (holder.extra as VB).onBindingHolder(holder)
    }

    abstract fun VB.onBindingHolder(holder: RecyclerHolder)

}