package me.ibore.ktx

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


//扩展函数
@ColorInt
fun Context.color(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

@ColorInt
fun View.color(@ColorRes colorRes: Int): Int = context.color(colorRes)