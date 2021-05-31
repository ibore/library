package me.ibore.float

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import me.ibore.R

class FloatingIconView @JvmOverloads constructor(@NonNull context: Context, @DrawableRes resId: Int = R.drawable.x_title_bar_back) :
        FloatingView(context, null) {

    private val mIcon: ImageView

    fun setIconImage(@DrawableRes resId: Int) {
        mIcon.setImageResource(resId)
    }

    init {
        mIcon = AppCompatImageView(context)
        mIcon.scaleType = ImageView.ScaleType.CENTER_CROP
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER
        addView(mIcon, layoutParams)
        mIcon.setImageResource(resId)
    }
}
