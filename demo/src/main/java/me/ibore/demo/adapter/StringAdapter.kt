package me.ibore.demo.adapter

import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.model.TitleItem
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.BindingHolder

class StringAdapter : BindingAdapter<ItemActivityBinding, String>() {

    override fun onBindingHolder(
        holder: BindingHolder<ItemActivityBinding>,
        binding: ItemActivityBinding,
        data: String,
        dataPosition: Int
    ) {
        binding.tvTitle.text = data
    }

}