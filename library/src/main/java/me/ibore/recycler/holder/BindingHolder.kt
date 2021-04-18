package me.ibore.recycler.holder

import androidx.viewbinding.ViewBinding

class BindingHolder<VB : ViewBinding>(val binding: VB) : RecyclerHolder(binding.root)