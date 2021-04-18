package me.ibore.viewpager2.pagetransformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2


abstract class BasePageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        retView(view)
        when {
            position < -1.0f -> {
                handleInvisiblePage(view, position)
            }
            position <= 0.0f -> {
                handleLeftPage(view, position)
            }
            position <= 1.0f -> {
                handleRightPage(view, position)
            }
            else -> {
                handleInvisiblePage(view, position)
            }
        }
    }

    private fun retView(view: View) {
        view.pivotX = 0F
        view.scaleX = 1.0f
        view.pivotY = 0F
        view.scaleY = 1.0f
        view.alpha = 1F
        view.rotationX = 0F
        view.rotationY = 0F
        view.rotation = 0F
        view.translationX = 0F
        view.translationY = 0F
    }

    abstract fun handleInvisiblePage(view: View, position: Float)
    abstract fun handleLeftPage(view: View, position: Float)
    abstract fun handleRightPage(view: View, position: Float)
}