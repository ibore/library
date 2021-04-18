package me.ibore.recycler.listener

import me.ibore.recycler.holder.RecyclerHolder

interface OnItemLongClickListener<VH : RecyclerHolder, D> {

    fun onItemLongClick(holder: VH, data: D, position: Int): Boolean

}