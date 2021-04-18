package me.ibore.viewpager2.pagetransformer

import android.view.View

class AlphaPageTransformer : BasePageTransformer {
    private var mMinScale = 0.4f

    constructor()

    constructor(minScale: Float) {
        setMinScale(minScale)
    }

    override fun handleInvisiblePage(view: View, position: Float) {
        view.alpha = 0F
    }

    override fun handleLeftPage(view: View, position: Float) {
        view.alpha = mMinScale + (1 - mMinScale) * (1 + position)
    }

    override fun handleRightPage(view: View, position: Float) {
        view.alpha = mMinScale + (1 - mMinScale) * (1 - position)
    }

    fun setMinScale(minScale: Float) {
        if (minScale in 0.0f..1.0f) {
            mMinScale = minScale
        }
    }
}