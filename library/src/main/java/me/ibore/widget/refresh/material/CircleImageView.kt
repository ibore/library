package me.ibore.widget.refresh.material

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat

@SuppressLint("WrongConstant")
class CircleImageView(context: Context?, color: Int, radius: Float) : AppCompatImageView(context!!) {
    private var mListener: Animation.AnimationListener? = null
    private var mShadowRadius: Int
    private fun elevationSupported(): Boolean {
        return Build.VERSION.SDK_INT >= 21
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!elevationSupported()) {
            setMeasuredDimension(measuredWidth + mShadowRadius * 2,
                    measuredHeight + mShadowRadius * 2)
        }
    }

    fun setAnimationListener(listener: Animation.AnimationListener?) {
        mListener = listener
    }

    public override fun onAnimationStart() {
        super.onAnimationStart()
        if (mListener != null) {
            mListener!!.onAnimationStart(animation)
        }
    }

    public override fun onAnimationEnd() {
        super.onAnimationEnd()
        if (mListener != null) {
            mListener!!.onAnimationEnd(animation)
        }
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    fun setBackgroundColorRes(colorRes: Int) {
        setBackgroundColor(context.resources.getColor(colorRes))
    }

    override fun setBackgroundColor(color: Int) {
        if (background is ShapeDrawable) {
            (background as ShapeDrawable).paint.color = color
        }
    }

    private inner class OvalShadow(shadowRadius: Int, circleDiameter: Int) : OvalShape() {
        private val mShadowPaint: Paint = Paint()
        private val mCircleDiameter: Int
        override fun draw(canvas: Canvas, paint: Paint) {
            val viewWidth = this@CircleImageView.width
            val viewHeight = this@CircleImageView.height
            canvas.drawCircle(viewWidth / 2f, viewHeight / 2f, mCircleDiameter / 2f + mShadowRadius,
                    mShadowPaint)
            canvas.drawCircle(viewWidth / 2f, viewHeight / 2f, mCircleDiameter / 2f, paint)
        }

        init {
            mShadowRadius = shadowRadius
            mCircleDiameter = circleDiameter
            val mRadialGradient = RadialGradient(mCircleDiameter / 2f, mCircleDiameter / 2f,
                    mShadowRadius.toFloat(), intArrayOf(FILL_SHADOW_COLOR, Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
            mShadowPaint.shader = mRadialGradient
        }
    }

    companion object {
        private const val KEY_SHADOW_COLOR = 0x1E000000
        private const val FILL_SHADOW_COLOR = 0x3D000000

        // PX
        private const val X_OFFSET = 0f
        private const val Y_OFFSET = 1.75f
        private const val SHADOW_RADIUS = 3.5f
        private const val SHADOW_ELEVATION = 4
    }

    init {
        val density = getContext().resources.displayMetrics.density
        val diameter = (radius * density * 2).toInt()
        val shadowYOffset = (density * Y_OFFSET).toInt()
        val shadowXOffset = (density * X_OFFSET).toInt()
        mShadowRadius = (density * SHADOW_RADIUS).toInt()
        val circle: ShapeDrawable
        if (elevationSupported()) {
            circle = ShapeDrawable(OvalShape())
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density)
        } else {
            val oval: OvalShape = OvalShadow(mShadowRadius, diameter)
            circle = ShapeDrawable(oval)
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.paint)
            circle.paint.setShadowLayer(mShadowRadius.toFloat(), shadowXOffset.toFloat(), shadowYOffset.toFloat(),
                    KEY_SHADOW_COLOR)
            val padding = mShadowRadius
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding)
        }
        circle.paint.color = color
        setBackgroundDrawable(circle)
    }
}