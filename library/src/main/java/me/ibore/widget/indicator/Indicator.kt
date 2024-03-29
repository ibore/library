package me.ibore.widget.indicator

import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.Px

/**
 * 可以实现该接口，自定义Indicator 可参考内置的[IndicatorView]
 */
interface Indicator {
    /**
     * Called when data initialization is complete
     *
     * @param pagerCount page num
     */
    fun initIndicatorCount(pagerCount: Int)

    /**
     * return View，and add banner
     */
    val view: View?

    /**
     * retuan RelativeLayout.LayoutParams，Set the position of the banner within the RelativeLayout
     */
    var params: RelativeLayout.LayoutParams?

    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     * Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    fun onPageScrolled(position: Int, positionOffset: Float, @Px positionOffsetPixels: Int)

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    fun onPageSelected(position: Int)

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
     *
     * @see androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
     *
     * @see androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
     */
    fun onPageScrollStateChanged(state: Int)
}
