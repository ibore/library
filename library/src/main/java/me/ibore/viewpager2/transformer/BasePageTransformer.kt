package me.ibore.viewpager2.transformer

import androidx.viewpager2.widget.ViewPager2

abstract class BasePageTransformer : ViewPager2.PageTransformer {
    companion object {
        const val DEFAULT_CENTER = 0.5f
    }
}