package me.ibore.recycler.delegate

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.ibore.ktx.layoutInflater
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.utils.ReflexUtils

abstract class DelegateBindingHolder<VB : ViewBinding, D : Delegate>(override val delegateType: Int) :
    DelegateHolder(delegateType) {

    override fun onCreateHolder(parent: ViewGroup): RecyclerHolder {
        val vb: VB = ReflexUtils.viewBinding(javaClass, parent.layoutInflater, parent, false)
        return RecyclerHolder(vb.root, vb)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindHolder(holder: RecyclerHolder, data: Delegate) {
        (holder.helper as VB).onBindHolder(holder, data as D)
    }

    abstract fun VB.onBindHolder(holder: RecyclerHolder, data: D)

}