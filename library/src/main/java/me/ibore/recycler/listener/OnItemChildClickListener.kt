package me.ibore.recycler.listener

import androidx.annotation.IdRes
import me.ibore.recycler.holder.RecyclerHolder

interface OnItemChildClickListener<VH : RecyclerHolder, D> {

    fun onItemClick(holder: VH, @IdRes id: Int, data: D, position: Int)

}