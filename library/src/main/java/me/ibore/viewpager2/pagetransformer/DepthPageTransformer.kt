package me.ibore.viewpager2.pagetransformer

import android.view.View

class DepthPageTransformer : BasePageTransformer {

    private var mMinScale = 0.8f

    constructor()

    constructor(minScale: Float) {
        setMinScale(minScale)
    }

    override fun handleInvisiblePage(view: View, position: Float) {
        view.alpha = 0f
    }

    override fun handleLeftPage(view: View, position: Float) {
        view.alpha = 1f
        view.translationX = 0f
        view.scaleX = 1f
        view.scaleY = 1f
    }

    override fun handleRightPage(view: View, position: Float) {
        view.alpha = 1 - position
        view.translationX = -view.width * position
        val scale = mMinScale + (1 - mMinScale) * (1 - position)
        view.scaleX = scale
        view.scaleY = scale
    }

    fun setMinScale(minScale: Float) {
        if (minScale in 0.6f..1.0f) {
            mMinScale = minScale
        }
    }
}