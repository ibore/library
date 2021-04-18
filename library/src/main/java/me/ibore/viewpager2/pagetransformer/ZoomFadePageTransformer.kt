package me.ibore.viewpager2.pagetransformer

import android.view.View


class ZoomFadePageTransformer : BasePageTransformer() {
    override fun handleInvisiblePage(view: View, position: Float) {}
    override fun handleLeftPage(view: View, position: Float) {
        view.translationX = -view.width * position
        view.pivotX = view.width * 0.5f
        view.pivotY = view.height * 0.5f
        view.scaleX = 1 + position
        view.scaleY = 1 + position
        view.alpha = 1 + position
    }

    override fun handleRightPage(view: View, position: Float) {
        view.translationX = -view.width * position
        view.pivotX = view.width * 0.5f
        view.pivotY = view.height * 0.5f
        view.scaleX = 1 - position
        view.scaleY = 1 - position
        view.alpha = 1 - position
    }
}