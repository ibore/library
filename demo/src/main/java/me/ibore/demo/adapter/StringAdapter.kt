package me.ibore.demo.adapter

import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder

class StringAdapter : BindingAdapter<ItemActivityBinding, String>() {

    override fun ItemActivityBinding.onBindHolder(
        holder: RecyclerHolder, data: String, dataPosition: Int
    ) {
        tvTitle.text = data
    }

}