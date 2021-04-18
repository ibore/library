package me.ibore.viewpager2.pagetransformer

import android.view.View
import androidx.core.view.ViewCompat


class ZoomPageTransformer : BasePageTransformer {
    private var mMinScale = 0.85f
    private var mMinAlpha = 0.65f

    constructor()
    constructor(minAlpha: Float, minScale: Float) {
        setMinAlpha(minAlpha)
        setMinScale(minScale)
    }

    override fun handleInvisiblePage(view: View, position: Float) {
        ViewCompat.setAlpha(view, 0f)
    }

    override fun handleLeftPage(view: View, position: Float) {
        val scale = mMinScale.coerceAtLeast(1 + position)
        val vertMargin = view.height * (1 - scale) / 2
        val horzMargin = view.width * (1 - scale) / 2
        view.translationX = horzMargin - vertMargin / 2
        view.scaleX = scale
        view.scaleY = scale
        view.alpha = mMinAlpha + (scale - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha)
    }

    override fun handleRightPage(view: View, position: Float) {
        val scale = mMinScale.coerceAtLeast(1 - position)
        val vertMargin = view.height * (1 - scale) / 2
        val horzMargin = view.width * (1 - scale) / 2
        view.translationX = -horzMargin + vertMargin / 2
        view.scaleX = scale
        view.scaleY = scale
        view.alpha = mMinAlpha + (scale - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha)
    }

    fun setMinAlpha(minAlpha: Float) {
        if (minAlpha in 0.6f..1.0f) {
            mMinAlpha = minAlpha
        }
    }

    fun setMinScale(minScale: Float) {
        if (minScale in 0.6f..1.0f) {
            mMinScale = minScale
        }
    }
}