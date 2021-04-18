package me.ibore.viewpager2.pagetransformer

import android.view.View


class CubePageTransformer : BasePageTransformer {

    private var mMaxRotation = 90.0f

    constructor()

    constructor(maxRotation: Float) {
        setMaxRotation(maxRotation)
    }

    override fun handleInvisiblePage(view: View, position: Float) {
        view.pivotX = view.measuredWidth.toFloat()
        view.pivotY = view.measuredHeight * 0.5f
        view.rotationY = 0F
    }

    override fun handleLeftPage(view: View, position: Float) {
        view.pivotX = view.measuredWidth.toFloat()
        view.pivotY = view.measuredHeight * 0.5f
        view.rotationY = mMaxRotation * position
    }

    override fun handleRightPage(view: View, position: Float) {
        view.pivotX = 0F
        view.pivotY = view.measuredHeight * 0.5f
        view.rotationY = mMaxRotation * position
    }

    fun setMaxRotation(maxRotation: Float) {
        if (maxRotation in 0.0f..90.0f) {
            mMaxRotation = maxRotation
        }
    }
}