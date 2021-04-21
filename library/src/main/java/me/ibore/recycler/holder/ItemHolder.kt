package me.ibore.recycler.holder

import android.view.ViewGroup

interface ItemHolder {

    fun onCreateHolder(parent: ViewGroup): RecyclerHolder

    fun onBindHolder(holder: RecyclerHolder)

}