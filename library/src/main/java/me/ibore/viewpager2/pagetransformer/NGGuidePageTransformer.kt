package me.ibore.viewpager2.pagetransformer

import android.view.View


class NGGuidePageTransformer : BasePageTransformer() {
    override fun handleInvisiblePage(view: View, position: Float) {}
    override fun handleLeftPage(view: View, position: Float) {
        val pageWidth = view.width //得到view宽
        //消失的页面
        view.translationX = -pageWidth * position //阻止消失页面的滑动
        // Fade the page relative to its size.
        val alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position))
        //透明度改变Log
        view.alpha = alphaFactor
    }

    override fun handleRightPage(view: View, position: Float) {
        val pageWidth = view.width //得到view宽
        //出现的页面
        view.translationX = pageWidth.toFloat() //直接设置出现的页面到底
        view.translationX = -pageWidth * position //阻止出现页面的滑动
        // Fade the page relative to its size.
        val alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position))
        //透明度改变Log
        view.alpha = alphaFactor
    }

    companion object {
        private const val MIN_ALPHA = 0.0f //最小透明度
    }
}