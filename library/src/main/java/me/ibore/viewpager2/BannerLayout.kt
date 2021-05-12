//package me.ibore.viewpager2
//
//import android.content.Context
//import android.util.AttributeSet
//import android.widget.FrameLayout
//import androidx.annotation.IntDef
//import androidx.viewpager2.widget.CompositePageTransformer
//import androidx.viewpager2.widget.ViewPager2
//import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
//import me.ibore.viewpager2.indicator.Indicator
//
//class BannerLayout @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : FrameLayout(context, attrs, defStyleAttr) {
//
//    companion object {
//        const val INVALID_VALUE = -1
//
//        const val HORIZONTAL = 0
//        const val VERTICAL = 1
//    }
//
//    @Retention(AnnotationRetention.SOURCE)
//    @IntDef(HORIZONTAL, VERTICAL)
//    annotation class Orientation
//
//    private val mViewPager2: ViewPager2? = null
//    private val mOnPageChangeListener: OnPageChangeListener? = null
//
//    private val mIndicator: Indicator? = null
//    private val mCompositePageTransformer: CompositePageTransformer? = null
//    private val mPageChangeCallback: BannerOnPageChangeCallback? = null
//
//
//
//    internal class BannerOnPageChangeCallback : OnPageChangeCallback() {
//        private var mTempPosition: Int = INVALID_VALUE
//        private var isScrolled = false
//        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//            val realPosition: Int =
//                BannerUtils.getRealPosition(isInfiniteLoop(), position, getRealCount())
//            if (mOnPageChangeListener != null) {
//                mOnPageChangeListener.onPageScrolled(
//                    realPosition,
//                    positionOffset,
//                    positionOffsetPixels
//                )
//            }
//            if (mIndicator != null) {
//                mIndicator.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
//            }
//        }
//
//        override fun onPageSelected(position: Int) {
//            if (isScrolled) {
//                mTempPosition = position
//                val realPosition: Int =
//                    BannerUtils.getRealPosition(isInfiniteLoop(), position, getRealCount())
//                if (mOnPageChangeListener != null) {
//                    mOnPageChangeListener.onPageSelected(realPosition)
//                }
//                if (mIndicator != null) {
//                    mIndicator.onPageSelected(realPosition)
//                }
//            }
//        }
//
//        override fun onPageScrollStateChanged(state: Int) {
//            //手势滑动中,代码执行滑动中
//            if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
//                isScrolled = true
//            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
//                //滑动闲置或滑动结束
//                isScrolled = false
//                if (mTempPosition != INVALID_VALUE && mIsInfiniteLoop) {
//                    if (mTempPosition == 0) {
//                        setCurrentItem(getRealCount(), false)
//                    } else if (mTempPosition == getItemCount() - 1) {
//                        setCurrentItem(1, false)
//                    }
//                }
//            }
//            if (mOnPageChangeListener != null) {
//                mOnPageChangeListener.onPageScrollStateChanged(state)
//            }
//            if (mIndicator != null) {
//                mIndicator.onPageScrollStateChanged(state)
//            }
//        }
//    }
//
//}