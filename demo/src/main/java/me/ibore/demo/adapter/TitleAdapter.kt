package me.ibore.demo.adapter

import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.model.TitleItem
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.BindingHolder

class TitleAdapter : BindingAdapter<ItemActivityBinding, TitleItem>() {

    override fun ItemActivityBinding.onBindingHolder(
        holder: BindingHolder<ItemActivityBinding>,
        data: TitleItem,
        dataPosition: Int
    ) {
        tvTitle.text = data.title
        root.setOnClickListener {
            data.unit.invoke()
        }
    }

}