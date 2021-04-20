package me.ibore.demo.adapter

import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.model.TitleItem
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.BindingHolder

class StringAdapter : BindingAdapter<ItemActivityBinding, String>() {

    override fun ItemActivityBinding.onBindingHolder(
        holder: BindingHolder<ItemActivityBinding>,
        data: String,
        dataPosition: Int
    ) {
        tvTitle.text = data
    }

}