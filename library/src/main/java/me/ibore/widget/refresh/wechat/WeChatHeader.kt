package me.ibore.widget.refresh.wechat

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import me.ibore.R
import me.ibore.ktx.dp2px
import me.ibore.widget.refresh.RefreshHeader
import me.ibore.widget.refresh.RefreshLayout

class WeChatHeader @JvmOverloads constructor(@NonNull context: Context, @Nullable attrs: AttributeSet? = null)
    : FrameLayout(context, attrs), RefreshHeader {

    private val imgChat: ImageView = ImageView(context)
    private val rotateAnimation = RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
    private val returnAnimator = ValueAnimator()

    override fun succeedRetention(): Long = 0
    override fun failingRetention(): Long = 0
    override fun refreshHeight(): Int = dp2px(50F)
    override fun maxOffsetHeight(): Int = (parent as View).height
    override fun onReset(refreshLayout: RefreshLayout) {}
    override fun onPrepare(refreshLayout: RefreshLayout) {}
    override fun onRefresh(refreshLayout: RefreshLayout) {
        imgChat.startAnimation(rotateAnimation)
    }

    override fun onComplete(refreshLayout: RefreshLayout, isSuccess: Boolean) {
        imgChat.clearAnimation()
        returnAnimator.setIntValues(mDistance, 0)
        returnAnimator.start()
    }

    var mDistance = 0
    private var lastDistance = 0
    override fun onScroll(refreshLayout: RefreshLayout, distance: Int, percent: Float, refreshing: Boolean) {
        var offset = distance - lastDistance
        if (returnAnimator.isRunning) returnAnimator.cancel()
        lastDistance = distance
        if (!refreshing) {
            imgChat.rotation = -distance.toFloat()
            if (percent > 1) {
                offsetTopAndBottom(-offset)
                if (mDistance != refreshHeight()) {
                    offset = refreshHeight() - mDistance
                    offsetTopAndBottom(offset)
                    mDistance += offset
                }
            } else {
                if (mDistance + offset != distance) {
                    offset = distance - (mDistance + offset)
                    offsetTopAndBottom(offset)
                }
                mDistance = distance
            }
        } else {
            offsetTopAndBottom(-offset)
        }
        refreshLayout.headerOffset = mDistance - distance
    }

    init {
        imgChat.setImageResource(R.drawable.x_refresh_wechat)
        val params = LayoutParams(dp2px(30F), dp2px(30F))
        params.leftMargin = dp2px(20F)
        addView(imgChat, params)
        rotateAnimation.duration = 800
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.repeatMode = Animation.RESTART
        returnAnimator.duration = 800
        returnAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            offsetTopAndBottom(progress - mDistance)
            imgChat.rotation = progress.toFloat()
            mDistance = progress
            if (parent is RefreshLayout) {
                (parent as RefreshLayout).headerOffset = mDistance - lastDistance
            }
        }
    }
}
