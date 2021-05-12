package me.ibore.viewpager2.transformer

import android.view.View

class AlphaPageTransformer @JvmOverloads constructor(private val mMinAlpha: Float = DEFAULT_MIN_ALPHA) :
    BasePageTransformer() {

    override fun transformPage(view: View, position: Float) {
        view.scaleX = 0.999f //hack
        if (position < -1) { // [-Infinity,-1)
            view.alpha = mMinAlpha
        } else if (position <= 1) { // [-1,1]
            //[0，-1]
            if (position < 0) {
                //[1,min]
                val factor = mMinAlpha + (1 - mMinAlpha) * (1 + position)
                view.alpha = factor
            } else { //[1，0]
                //[min,1]
                val factor = mMinAlpha + (1 - mMinAlpha) * (1 - position)
                view.alpha = factor
            }
        } else { // (1,+Infinity]
            view.alpha = mMinAlpha
        }
    }

    companion object {
        private const val DEFAULT_MIN_ALPHA = 0.5f
    }
}