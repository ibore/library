package me.ibore.recycler.holder

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.ibore.ktx.layoutInflater
import me.ibore.utils.ReflexUtils

abstract class ItemBindingHolder<VB : ViewBinding> : ItemHolder {

    override fun onCreateHolder(parent: ViewGroup): RecyclerHolder {
        val vb: VB = ReflexUtils.viewBinding(javaClass, parent.layoutInflater, parent, false)
        return RecyclerHolder(vb.root, vb)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindHolder(holder: RecyclerHolder) {
        (holder.helper as VB).onBindHolder(holder)
    }

    abstract fun VB.onBindHolder(holder: RecyclerHolder)

}