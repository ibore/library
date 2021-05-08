package me.ibore.widget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import me.ibore.base.XActivity

abstract class ILayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    fun getXActivity(): XActivity<*>? {
        return ContextUtils.getXActivity(context)
    }
}
