package me.ibore.widget. drawable

import android.graphics.*
import android.graphics.drawable.Drawable


class RoundDrawable(private val mBitmap: Bitmap) : Drawable() {

    private val mPaint: Paint
    private var mRectF: RectF? = null

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(mRectF!!, 30F, 30F, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    //返回图片实际的宽高
    override fun getIntrinsicWidth(): Int {
        return mBitmap.width
    }

    override fun getIntrinsicHeight(): Int {
        return mBitmap.height
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        mRectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    init {
        //着色器，设置横向纵向平铺
        val bitmapShader = BitmapShader(mBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.shader = bitmapShader
    }
}