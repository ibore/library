package me.ibore.recycler.listener

import me.ibore.recycler.holder.RecyclerHolder

interface OnItemClickListener<VH : RecyclerHolder, D> {

    fun onItemClick(holder: VH, data: D, position: Int)

}