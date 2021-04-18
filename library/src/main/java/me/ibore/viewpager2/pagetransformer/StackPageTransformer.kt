package me.ibore.viewpager2.pagetransformer

import android.view.View


class StackPageTransformer : BasePageTransformer() {
    override fun handleInvisiblePage(view: View, position: Float) {}
    override fun handleLeftPage(view: View, position: Float) {}
    override fun handleRightPage(view: View, position: Float) {
        view.translationX = -view.width * position
    }
}