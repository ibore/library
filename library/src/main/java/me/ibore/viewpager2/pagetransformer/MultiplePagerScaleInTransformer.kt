package me.ibore.viewpager2.pagetransformer

import android.view.View
import androidx.annotation.Px
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2


class MultiplePagerScaleInTransformer(@param:Px private val marginPx: Int, private val scale: Float) : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val viewPager = requireViewPager(page)
        val offset = position * marginPx
        if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                page.translationX = offset
            } else {
                page.translationX = -offset
            }
            page.scaleY = 1 - scale * Math.abs(position)
        } else {
            page.translationY = -offset
            page.scaleX = 1 - scale * Math.abs(position)
        }
    }

    private fun requireViewPager(page: View): ViewPager2 {
        val parent = page.parent
        val parentParent = parent.parent
        if (parent is RecyclerView && parentParent is ViewPager2) {
            return parentParent
        }
        throw IllegalStateException(
                "Expected the page view to be managed by a ViewPager2 instance.")
    }

}