package me.ibore.viewpager2.pagetransformer

import android.view.View

class AccordionPageTransformer : BasePageTransformer() {

    override fun handleInvisiblePage(view: View, position: Float) {}
    override fun handleLeftPage(view: View, position: Float) {
        view.pivotX = view.width.toFloat()
        view.scaleX = 1.0f + position
    }

    override fun handleRightPage(view: View, position: Float) {
        view.pivotX = 0f
        view.scaleX = 1.0f - position
        view.alpha = 1f
    }
}