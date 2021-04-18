package me.ibore.widget.refresh.classical

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import me.ibore.R
import me.ibore.ktx.dp2px
import me.ibore.widget.refresh.RefreshHeader
import me.ibore.widget.refresh.RefreshLayout

class ClassicalHeader @JvmOverloads constructor(@NonNull context: Context, @Nullable attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr), RefreshHeader {

    private val arrowImage: ImageView
    private val textTitle: TextView
    private val rotateAnimation = RotateAnimation(0F, 360F,
            Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5f)

    init {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.HORIZONTAL
        root.gravity = Gravity.CENTER_VERTICAL
        addView(root, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        (root.layoutParams as LayoutParams).gravity = Gravity.CENTER
        arrowImage = ImageView(context)
        arrowImage.setImageResource(R.drawable.refresh_arrow_down)
        arrowImage.scaleType = ImageView.ScaleType.CENTER
        root.addView(arrowImage)
        textTitle = TextView(context)
        textTitle.textSize = 13f
        textTitle.setText(R.string.refresh_pull_down_to_refresh)
        textTitle.setTextColor(Color.parseColor("#999999"))
        val params = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.leftMargin = 20
        root.addView(textTitle, params)
        rotateAnimation.duration = 800
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.repeatMode = Animation.RESTART
        setPadding(0, dp2px(16F), 0, dp2px(16F))
    }

    override fun succeedRetention(): Long = 200

    override fun failingRetention(): Long = 0

    override fun refreshHeight(): Int = height

    override fun maxOffsetHeight(): Int = 4 * height

    var isReset = true

    override fun onReset(refreshLayout: RefreshLayout) {
        arrowImage.setImageResource(R.drawable.refresh_arrow_down)
        textTitle.setText(R.string.refresh_pull_down_to_refresh)
        isReset = true
        arrowImage.visibility = View.VISIBLE
    }

    override fun onPrepare(refreshLayout: RefreshLayout) {
        arrowImage.setImageResource(R.drawable.refresh_arrow_down)
        textTitle.setText(R.string.refresh_pull_down_to_refresh)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        arrowImage.setImageResource(R.drawable.refresh_loading)
        arrowImage.startAnimation(rotateAnimation)
        textTitle.setText(R.string.refresh_loading)
        isReset = false
    }

    override fun onComplete(refreshLayout: RefreshLayout, isSuccess: Boolean) {
        arrowImage.clearAnimation()
        arrowImage.visibility = View.GONE
        if (isSuccess) textTitle.setText(R.string.refresh_refresh_complete)
        else textTitle.setText(R.string.refresh_refresh_failed)
    }

    var attain = false

    override fun onScroll(refreshLayout: RefreshLayout, distance: Int, percent: Float, refreshing: Boolean) {
        if (!refreshing && isReset) {
            if (percent >= 1 && !attain) {
                attain = true
                textTitle.setText(R.string.refresh_release_refresh)
                arrowImage.animate().rotation(-180F).start()
            } else if (percent < 1 && attain) {
                attain = false
                arrowImage.animate().rotation(0F).start()
                textTitle.setText(R.string.refresh_pull_down_to_refresh)
            }
        }
    }

}
