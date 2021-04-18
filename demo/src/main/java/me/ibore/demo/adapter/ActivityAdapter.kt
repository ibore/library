package me.ibore.demo.adapter

import android.content.Intent
import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.model.ActivityItem
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.BindingHolder

class ActivityAdapter : BindingAdapter<ItemActivityBinding, ActivityItem>() {

    override fun onBindingHolder(holder: BindingHolder<ItemActivityBinding>, binding: ItemActivityBinding, data: ActivityItem, dataPosition: Int) {
        binding.tvTitle.text = data.title
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, data.clazz)
            intent.putExtra("title", data.title)
            binding.root.context.startActivity(intent)
        }
    }

}