package me.ibore.widget.drawable

import android.graphics.*
import android.graphics.drawable.Drawable


class CircleDrawable(mBitmap: Bitmap) : Drawable() {
    private val mPaint: Paint
    private val mWidth: Int

    //绘制
    override fun draw(canvas: Canvas) {
        canvas.drawCircle(mWidth / 2F, mWidth / 2F, mWidth / 2F, mPaint)
    }

    //设置透明度值
    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    //设置yans颜色过滤器
    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    //返回不透明度
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    //返回图片实际的宽高
    override fun getIntrinsicWidth(): Int {
        return mWidth
    }

    override fun getIntrinsicHeight(): Int {
        return mWidth
    }

    init {
        //着色器，设置横向纵向平铺
        val bitmapShader = BitmapShader(mBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.shader = bitmapShader
        mWidth = mBitmap.width.coerceAtMost(mBitmap.height)
    }
}