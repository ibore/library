package me.ibore.demo.adapter

import android.content.Intent
import me.ibore.demo.databinding.ItemActivityBinding
import me.ibore.demo.model.ActivityItem
import me.ibore.recycler.adapter.BindingAdapter
import me.ibore.recycler.holder.RecyclerHolder

class ActivityAdapter : BindingAdapter<ItemActivityBinding, ActivityItem>() {

    override fun ItemActivityBinding.onBindHolder(
        holder: RecyclerHolder, data: ActivityItem, dataPosition: Int) {
        tvTitle.text = data.title
        root.setOnClickListener {
            val intent = Intent(root.context, data.clazz)
            intent.putExtra("title", data.title)
            root.context.startActivity(intent)
        }
    }

}