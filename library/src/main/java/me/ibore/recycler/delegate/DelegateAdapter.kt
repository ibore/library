package me.ibore.recycler.delegate

import android.util.ArrayMap
import android.view.ViewGroup
import me.ibore.recycler.adapter.RecyclerAdapter
import me.ibore.recycler.holder.RecyclerHolder

open class DelegateAdapter(delegateHolders: MutableList<DelegateHolder>) :
    RecyclerAdapter<Delegate>() {

    private var delegateHoldersMap = ArrayMap<Int, DelegateHolder>()

    init {
        for (delegateHolder in delegateHolders) {
            delegateHoldersMap[delegateHolder.delegateType] = delegateHolder
        }
    }

    override fun onCreateHolder(parent: ViewGroup, dataType: Int): RecyclerHolder {
        return delegateHoldersMap[dataType]!!.onCreateHolder(parent)
    }

    override fun getDataType(data: Delegate, dataPosition: Int): Int {
        return data.delegateType
    }

    override fun onBindHolder(
        holder: RecyclerHolder, data: Delegate, dataPosition: Int, viewType: Int
    ) {
        delegateHoldersMap[getDataType(data, dataPosition)]?.onBindHolder(holder, data)
    }

}
