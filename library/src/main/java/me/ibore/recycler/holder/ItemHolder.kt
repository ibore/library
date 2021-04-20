package me.ibore.recycler.holder

import android.view.ViewGroup

interface ItemHolder<VH : RecyclerHolder> {

    fun onCreateHolder(parent: ViewGroup): VH

    fun onBindHolder(holder: VH)

}