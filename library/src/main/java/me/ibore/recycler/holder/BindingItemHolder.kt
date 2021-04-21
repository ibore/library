package me.ibore.recycler.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.ibore.utils.BindingUtils

abstract class BindingItemHolder<VB : ViewBinding> : ItemHolder {

    override fun onCreateHolder(parent: ViewGroup): RecyclerHolder {
        val vb: VB = BindingUtils.reflexViewBinding(
            javaClass, LayoutInflater.from(parent.context), parent, false
        )
        return BindingHolder(vb)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindHolder(holder: RecyclerHolder) {
        (holder as BindingHolder<VB>).binding.onBindingHolder(holder)
    }

    abstract fun VB.onBindingHolder(holder: BindingHolder<VB>)

}