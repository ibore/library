package me.ibore.viewpager2.pagetransformer

import android.view.View

class FlipPageTransformer : BasePageTransformer() {
    override fun handleInvisiblePage(view: View, position: Float) {}
    override fun handleLeftPage(view: View, position: Float) {
        view.translationX = -view.width * position
        val rotation = ROTATION * position
        view.rotationY = rotation
        if (position > -0.5) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    override fun handleRightPage(view: View, position: Float) {
        view.translationX = -view.width * position
        val rotation = ROTATION * position
        view.rotationY = rotation
        if (position < 0.5) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val ROTATION = 180.0f
    }
}