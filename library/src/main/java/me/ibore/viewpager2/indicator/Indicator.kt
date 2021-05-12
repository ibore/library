package me.ibore.viewpager2.indicator

import android.view.View
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.viewpager2.widget.ViewPager2.ScrollState

interface Indicator {

    val view: View

    val layoutParams: FrameLayout.LayoutParams

    fun onPageScrolled(position: Int, positionOffset: Float, @Px positionOffsetPixels: Int)

    fun onPageSelected(position: Int)

    fun onPageScrollStateChanged(@ScrollState state: Int)

}