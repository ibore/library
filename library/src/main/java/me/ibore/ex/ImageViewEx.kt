package me.ibore.ex

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.load(url: String) {
    Glide.with(this).load(url).into(this)
}

fun ImageView.load(url: String, options: RequestOptions) {
    Glide.with(this).load(url).apply(options).into(this)
}