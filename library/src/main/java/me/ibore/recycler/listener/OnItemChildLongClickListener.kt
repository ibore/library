package me.ibore.recycler.listener

import androidx.annotation.IdRes
import me.ibore.recycler.holder.RecyclerHolder

interface OnItemChildLongClickListener<H : RecyclerHolder, D> {

    fun onItemLongClick(holder: H, @IdRes idRes: Int, data: D, dataPosition: Int): Boolean

}