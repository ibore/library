package me.ibore.widget.refresh.material

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.*
import androidx.core.view.ViewCompat
import me.ibore.R
import me.ibore.ktx.dp2px
import me.ibore.widget.refresh.RefreshHeader
import me.ibore.widget.refresh.RefreshLayout

class MaterialHeader @JvmOverloads constructor(@NonNull context: Context, @Nullable attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr), RefreshHeader {

    private var mCircleWidth = 0
    private var mCircleHeight = 0
    private val CIRCLE_DIAMETER = 40
    private val CIRCLE_DIAMETER_LARGE = 56

    // Maps to ProgressBar.Large style
    val LARGE: Int = MaterialDrawable.LARGE

    // Maps to ProgressBar default style
    val DEFAULT: Int = MaterialDrawable.DEFAULT

    // Default background for the progress spinner
    private val CIRCLE_BG_LIGHT = -0x50506

    // Default offset in dips from the top of the view to where the progress spinner should stop
    private val DEFAULT_CIRCLE_TARGET = 64
    private val MAX_PROGRESS_ANGLE = 0.8f

    private val MAX_ALPHA = 255
    private val STARTING_PROGRESS_ALPHA = (.3f * MAX_ALPHA).toInt()

    private var mCircleView: CircleImageView
    private var mProgress: MaterialDrawable

    init {
        val metrics = resources.displayMetrics
        mCircleWidth = (CIRCLE_DIAMETER * metrics.density).toInt()
        mCircleHeight = (CIRCLE_DIAMETER * metrics.density).toInt()
        mCircleView = CircleImageView(context, CIRCLE_BG_LIGHT, (CIRCLE_DIAMETER / 2).toFloat())
        mProgress = MaterialDrawable(context, this)
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT)
        mCircleView.setImageDrawable(mProgress)
        mCircleView.visibility = GONE
        val params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        mCircleView.layoutParams = params
        addView(mCircleView)
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)
        setColorSchemeColors(*resources.getIntArray(R.array.refresh_color))
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param color
     */
    fun setProgressBackgroundColorSchemeColor(@ColorInt color: Int) {
        mCircleView.setBackgroundColor(color)
        mProgress.setBackgroundColor(color)
    }

    fun setColorSchemeResources(@ColorRes vararg colorResIds: Int) {
        val res = resources
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices) {
            colorRes[i] = res.getColor(colorResIds[i])
        }
        setColorSchemeColors(*colorRes)
    }

    fun setColorSchemeColors(vararg colors: Int) {
        mProgress.setColorSchemeColors(*colors)
    }

    fun setSize(size: Int) {
        if (size != LARGE && size != DEFAULT) {
            return
        }
        val metrics = resources.displayMetrics
        if (size == LARGE) {
            mCircleWidth = (CIRCLE_DIAMETER_LARGE * metrics.density).toInt()
            mCircleHeight = mCircleWidth
        } else {
            mCircleWidth = (CIRCLE_DIAMETER * metrics.density).toInt()
            mCircleHeight = mCircleWidth
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView.setImageDrawable(null)
        mProgress.updateSizes(size)
        mCircleView.setImageDrawable(mProgress)
    }

    override fun succeedRetention(): Long {
        return 200
    }

    override fun failingRetention(): Long {
        return 0
    }

    override fun refreshHeight(): Int {
        return height
    }

    override fun maxOffsetHeight(): Int {
        return 2 * height
    }

    var isReset = true

    override fun onReset(refreshLayout: RefreshLayout) {
        mCircleView.clearAnimation()
        mCircleView.animate().cancel()
        mProgress.stop()
        mCircleView.visibility = GONE
        mCircleView.background.alpha = MAX_ALPHA
        mProgress.alpha = MAX_ALPHA
        mCircleView.scaleX = 0F
        mCircleView.scaleY = 0F
        mCircleView.alpha = 1F
        isReset = true
    }

    override fun onPrepare(refreshLayout: RefreshLayout) {
        mProgress.alpha = STARTING_PROGRESS_ALPHA
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mCircleView.visibility = VISIBLE
        mCircleView.background.alpha = MAX_ALPHA
        mProgress.alpha = MAX_ALPHA
        mCircleView.scaleX = 1F
        mCircleView.scaleY = 1F
        mProgress.setArrowScale(1f)
        mProgress.start()
        isReset = false
    }

    override fun onComplete(refreshLayout: RefreshLayout, isSuccess: Boolean) {
        mCircleView.animate().scaleX(0F).scaleY(0F).alpha(0F).start()
    }

    override fun onScroll(refreshLayout: RefreshLayout, distance: Int, percent: Float, refreshing: Boolean) {
        if (!refreshing && isReset) {
            if (mCircleView.visibility != VISIBLE) {
                mCircleView.visibility = VISIBLE
            }
            if (percent >= 1f) {
                mCircleView.scaleX = 1F
                mCircleView.scaleY = 1F
            } else {
                mCircleView.scaleX = percent
                mCircleView.scaleY = percent
            }
            if (percent <= 1f) {
                mProgress.alpha = (STARTING_PROGRESS_ALPHA + (MAX_ALPHA - STARTING_PROGRESS_ALPHA) * percent).toInt()
            }
            val adjustedPercent = (percent - 0.4).coerceAtLeast(0.0).toFloat() * 5 / 3
            val strokeStart = adjustedPercent * 0.8f
            mProgress.setStartEndTrim(0f, MAX_PROGRESS_ANGLE.coerceAtMost(strokeStart))
            mProgress.setArrowScale(1f.coerceAtMost(adjustedPercent))
            val rotation = (-0.25f + 0.4f * adjustedPercent) * 0.5f
            mProgress.setProgressRotation(rotation)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeHeight = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            dp2px(40F)
        }
        super.onMeasure(widthMeasureSpec, sizeHeight)
    }
}