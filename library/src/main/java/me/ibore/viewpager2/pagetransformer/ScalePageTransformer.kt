package me.ibore.viewpager2.pagetransformer

import android.view.View


class ScalePageTransformer : BasePageTransformer() {
    override fun handleInvisiblePage(view: View, position: Float) {
        view.scaleY = MIN_SCALE
    }

    override fun handleLeftPage(view: View, position: Float) {
        val scale = Math.max(MIN_SCALE, 1 - Math.abs(position))
        view.scaleY = scale
    }

    override fun handleRightPage(view: View, position: Float) {
        val scale = Math.max(MIN_SCALE, 1 - Math.abs(position))
        view.scaleY = scale
    }

    companion object {
        private const val MIN_SCALE = 0.9f
    }
}