package me.ibore.recycler.adapter

import android.view.ViewGroup
import me.ibore.recycler.holder.RecyclerHolder

interface IRecyclerAdapter<D> {

    fun setDatas(datas: MutableList<D>)

    fun addData(data: D)

    fun addData(data: D, dataPosition: Int)

    fun addDatas(datas: MutableList<D>)

    fun getData(dataPosition: Int): D

    fun getDatas(): MutableList<D>

    fun getDataSize(): Int

    fun removeData(dataPosition: Int)

    fun removeData(data: D)

    fun clearDatas()

    fun onCreateHolder(parent: ViewGroup, dataType: Int): RecyclerHolder

    fun getDataType(data: D, dataPosition: Int): Int

    fun onBindHolder(holder: RecyclerHolder, data: D, dataPosition: Int, viewType: Int)

}