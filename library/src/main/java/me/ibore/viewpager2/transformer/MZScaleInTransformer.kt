package me.ibore.viewpager2.transformer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * 内部实现魅族效果使用的，单独使用可能效果不一定好，自己可以尝试下看看是否满意，推荐使用ScaleInTransformer
 */
class MZScaleInTransformer @JvmOverloads constructor(private val mMinScale: Float = DEFAULT_MIN_SCALE) :
    BasePageTransformer() {

    override fun transformPage(view: View, position: Float) {
        val viewPager = requireViewPager(view)
        val paddingLeft = viewPager.paddingLeft.toFloat()
        val paddingRight = viewPager.paddingRight.toFloat()
        val width = viewPager.measuredWidth.toFloat()
        val offsetPosition = paddingLeft / (width - paddingLeft - paddingRight)
        val currentPos = position - offsetPosition
        var reduceX = 0f
        val itemWidth = view.width.toFloat()
        //由于左右边的缩小而减小的x的大小的一半
        reduceX = (1.0f - mMinScale) * itemWidth / 2.0f
        if (currentPos <= -1.0f) {
            view.translationX = reduceX
            view.scaleX = mMinScale
            view.scaleY = mMinScale
        } else if (currentPos <= 1.0) {
            val scale = (1.0f - mMinScale) * Math.abs(1.0f - Math.abs(currentPos))
            val translationX = currentPos * -reduceX
            if (currentPos <= -0.5) { //两个view中间的临界，这时两个view在同一层，左侧View需要往X轴正方向移动覆盖的值()
                view.translationX =
                    translationX + Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f
            } else if (currentPos <= 0.0f) {
                view.translationX = translationX
            } else if (currentPos >= 0.5) { //两个view中间的临界，这时两个view在同一层
                view.translationX =
                    translationX - Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f
            } else {
                view.translationX = translationX
            }
            view.scaleX = scale + mMinScale
            view.scaleY = scale + mMinScale
        } else {
            view.scaleX = mMinScale
            view.scaleY = mMinScale
            view.translationX = -reduceX
        }
    }

    private fun requireViewPager(page: View): ViewPager2 {
        val parent = page.parent
        val parentParent = parent.parent
        if (parent is RecyclerView && parentParent is ViewPager2) {
            return parentParent
        }
        throw IllegalStateException(
            "Expected the page view to be managed by a ViewPager2 instance."
        )
    }

    companion object {
        private const val DEFAULT_MIN_SCALE = 0.85f
    }
}