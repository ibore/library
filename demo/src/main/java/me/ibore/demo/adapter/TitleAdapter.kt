package me.ibore.demo.adapter

import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.model.TitleItem
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder

class TitleAdapter : BindingAdapter<ItemActivityBinding, TitleItem>() {

    override fun ItemActivityBinding.onBindHolder(
        holder: RecyclerHolder, data: TitleItem, dataPosition: Int
    ) {
        tvTitle.text = data.title
        root.setOnClickListener {
            data.unit.invoke()
        }
    }

}