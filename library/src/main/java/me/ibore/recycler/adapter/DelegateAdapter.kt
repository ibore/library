package me.ibore.recycler.adapter

import android.view.ViewGroup
import me.ibore.recycler.holder.DelegateHolder
import me.ibore.recycler.holder.RecyclerHolder

//open class DelegateAdapter : RecyclerAdapter<RecyclerHolder, DelegateAdapter.Delegate>() {
//
//    companion object {
//        fun delegate(itemType: Int, delegateHolder: DelegateHolder): DelegateAdapter {
//            return DelegateAdapter().delegate(itemType, delegateHolder)
//        }
//    }
//
//    private var delegateHolders = LinkedHashMap<Int, DelegateHolder>()
//
//    fun delegate(itemType: Int, delegateHolder: DelegateHolder): DelegateAdapter {
//        delegateHolders[itemType] = delegateHolder
//        return this
//    }
//
//    override fun onCreateHolder(parent: ViewGroup, dataType: Int): RecyclerHolder {
//        return RecyclerHolder.create(parent, delegateHolders[dataType]!!.getLayoutId())
//    }
//
//    override fun getDataType(data: Delegate, dataPosition: Int): Int {
//        return data.itemType
//    }
//
//    override fun onBindHolder(holder: RecyclerHolder, data: Delegate, dataPosition: Int, viewType: Int) {
//        delegateHolders[getDataType(data, dataPosition)]!!.onBindView(holder, data)
//    }
//
//    interface Delegate {
//        val itemType: Int
//    }
//
//    class SimpleDelegate(override val itemType: Int) : Delegate
//}