package me.ibore.viewpager2.pagetransformer

import android.view.View


class RotatePageTransformer : BasePageTransformer {
    private var mMaxRotation = 15.0f

    constructor()
    constructor(maxRotation: Float) {
        setMaxRotation(maxRotation)
    }

    override fun handleInvisiblePage(view: View, position: Float) {
        view.pivotX = view.measuredWidth * 0.5f
        view.pivotY = view.measuredHeight.toFloat()
        view.rotation = 0f
    }

    override fun handleLeftPage(view: View, position: Float) {
        val rotation = mMaxRotation * position
        view.pivotX = view.measuredWidth * 0.5f
        view.pivotY = view.measuredHeight.toFloat()
        view.rotation = rotation
    }

    override fun handleRightPage(view: View, position: Float) {
        handleLeftPage(view, position)
    }

    fun setMaxRotation(maxRotation: Float) {
        if (maxRotation in 0.0f..40.0f) {
            mMaxRotation = maxRotation
        }
    }
}