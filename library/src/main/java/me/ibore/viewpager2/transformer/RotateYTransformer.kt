package me.ibore.viewpager2.transformer

import android.view.View

class RotateYTransformer @JvmOverloads constructor(private val mMaxRotate: Float = DEFAULT_MAX_ROTATE) :
    BasePageTransformer() {

    override fun transformPage(view: View, position: Float) {
        view.pivotY = (view.height / 2).toFloat()
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.rotationY = -1 * mMaxRotate
            view.pivotX = view.width.toFloat()
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            view.rotationY = position * mMaxRotate

            //[0,-1]
            if (position < 0) {
                view.pivotX =
                    view.width * (BasePageTransformer.Companion.DEFAULT_CENTER + BasePageTransformer.Companion.DEFAULT_CENTER * -position)
                view.pivotX = view.width.toFloat()
            } else { //[1,0]
                view.pivotX =
                    view.width * BasePageTransformer.Companion.DEFAULT_CENTER * (1 - position)
                view.pivotX = 0f
            }

            // Scale the page down (between MIN_SCALE and 1)
        } else {
            // (1,+Infinity]
            // This page is way off-screen to the right.
            view.rotationY = 1 * mMaxRotate
            view.pivotX = 0f
        }
    }

    companion object {
        private const val DEFAULT_MAX_ROTATE = 35f
    }
}