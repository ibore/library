package me.ibore.recycler.listener

import me.ibore.recycler.holder.RecyclerHolder


interface OnItemDragListener<VH : RecyclerHolder> {

    fun onItemDragStart(holder: VH, position: Int)

    fun onItemDragMoving(source: VH, fromPosition: Int, target: VH, toPosition: Int)

    fun onItemDragEnd(holder: VH, position: Int)

}