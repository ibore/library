package me.ibore.recycler.delegate

import android.view.ViewGroup
import me.ibore.recycler.holder.RecyclerHolder

abstract class DelegateHolder(open val delegateType: Int) {

    abstract fun onCreateHolder(parent: ViewGroup): RecyclerHolder

    abstract fun onBindHolder(holder: RecyclerHolder, data: Delegate)

}