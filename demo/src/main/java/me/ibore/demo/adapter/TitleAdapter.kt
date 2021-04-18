package me.ibore.demo.adapter

import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.model.TitleItem
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.BindingHolder

class TitleAdapter : BindingAdapter<ItemActivityBinding, TitleItem>() {

    override fun onBindingHolder(
        holder: BindingHolder<ItemActivityBinding>,
        binding: ItemActivityBinding,
        data: TitleItem,
        dataPosition: Int
    ) {
        binding.tvTitle.text = data.title
        binding.root.setOnClickListener {
            data.unit.invoke()
        }
    }

}