package me.ibore.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Nullable
import androidx.core.widget.NestedScrollView

class XNestedScrollView @JvmOverloads constructor(context: Context, @Nullable attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : NestedScrollView(context, attrs, defStyleAttr)  {

    private var onScrollChangeListener: OnScrollChangeListener? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        this.onScrollChangeListener?.onScrollChange(this, l, t, oldl, oldt)
    }

    fun setOnScrollChangeListener(onScrollChangeListener: OnScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener
    }

    interface OnScrollChangeListener {
        fun onScrollChange(view: XNestedScrollView, l: Int, t: Int, oldl: Int, oldt: Int)
    }
}
