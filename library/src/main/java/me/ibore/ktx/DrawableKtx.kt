package me.ibore.ktx

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat


fun Context.drawable(drawableRes: Int) = ContextCompat.getDrawable(this, drawableRes)

fun View.drawable(drawableRes: Int) = context.drawable(drawableRes)